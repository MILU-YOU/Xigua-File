package team.zlg.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import team.zlg.file.common.RestResult;
import team.zlg.file.constant.FileConstant;
import team.zlg.file.dto.DownloadFileDTO;
import team.zlg.file.dto.UploadFileDTO;
import team.zlg.file.model.File;
import team.zlg.file.model.Storage;
import team.zlg.file.model.User;
import team.zlg.file.model.UserFile;
import team.zlg.file.service.FileService;
import team.zlg.file.service.FiletransferService;
import team.zlg.file.service.UserService;
import team.zlg.file.service.UserfileService;
import team.zlg.file.util.DateUtil;
import team.zlg.file.util.FileUtil;
import team.zlg.file.util.RSA;
import team.zlg.file.vo.UploadFileVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {
    @Resource
    UserService userService;
    @Resource
    FileService fileService;
    @Resource
    UserfileService userfileService;
    @Resource
    FiletransferService filetransferService;

    @Operation(summary = "普通文件极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @GetMapping(value = "/uploadfile")
    public RestResult<UploadFileVO> uploadFileSpeed(UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        User sessionUser = userService.getUserByToken(token);
        if (sessionUser == null){

            return RestResult.fail().message("未登录");
        }

        String fileName = uploadFileDto.getFilename();
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper();
        userFileLambdaQueryWrapper.eq(UserFile::getFilePath,uploadFileDto.getFilePath())
                .eq(UserFile::getFileName,fileName.substring(0, fileName.lastIndexOf(".")))
                .eq(UserFile::getExtendName,FileUtil.getFileExtendName(fileName))
                .eq(UserFile::getUserId,sessionUser.getUserId())
                .eq(UserFile::getDeleteFlag,0);
        List<UserFile> userfiles = userfileService.list(userFileLambdaQueryWrapper);
        if(!userfiles.isEmpty()){
            return RestResult.fail().message("此位置已经包含同名文件");
        }

        UploadFileVO uploadFileVo = new UploadFileVO();
        //查询数据库中是否有与上传文件相同的identifier，如果是则直接上传成功，设置用户文件相关属性保存到数据库，并返回跳过上传
        synchronized (FiletransferController.class) {
            //当数据库中有相同identifier，且pointCount>0时则走极速上传
            LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fileLambdaQueryWrapper.eq(File::getIdentifier,uploadFileDto.getIdentifier())
                    .isNull(File::getFileKey)
                    .gt(File::getPointCount,0);
            List<File> list = fileService.list(fileLambdaQueryWrapper);

            //List<File> list = fileService.listByMap(param);
            if (list != null && !list.isEmpty()) {
                File file = list.get(0);

                UserFile userfile = new UserFile();
                userfile.setFileId(file.getFileId());
                userfile.setUserId(sessionUser.getUserId());
                userfile.setFilePath(uploadFileDto.getFilePath());
                userfile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                userfile.setExtendName(FileUtil.getFileExtendName(fileName));
                userfile.setIsDir(0);
                userfile.setUploadTime(DateUtil.getCurrentTime());
                userfile.setDeleteFlag(0);
                userfileService.save(userfile);

                //每有一个人上传已存在的文件，将file的pointCount+1
                fileService.increaseFilePointCount(file);

                uploadFileVo.setSkipUpload(true);

            } else {
                uploadFileVo.setSkipUpload(false);

            }
        }
        return RestResult.success().data(uploadFileVo);
    }

    @Operation(summary = "加密文件极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @GetMapping(value = "/encryptedupload")
    public RestResult<UploadFileVO> encrypteduploadFileSpeed(UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        User sessionUser = userService.getUserByToken(token);
        if (sessionUser == null){

            return RestResult.fail().message("未登录");
        }

        String fileName = uploadFileDto.getFilename();
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper();
        userFileLambdaQueryWrapper.eq(UserFile::getFilePath,uploadFileDto.getFilePath())
                .eq(UserFile::getFileName,fileName.substring(0, fileName.lastIndexOf(".")))
                .eq(UserFile::getExtendName,FileUtil.getFileExtendName(fileName))
                .eq(UserFile::getUserId,sessionUser.getUserId())
                .eq(UserFile::getDeleteFlag,0);
        List<UserFile> userfiles = userfileService.list(userFileLambdaQueryWrapper);
        if(!userfiles.isEmpty()){
            return RestResult.fail().message("此位置已经包含同名文件");
        }

        UploadFileVO uploadFileVo = new UploadFileVO();
        //查询数据库中是否有与上传文件相同的identifier，如果是则直接上传成功，设置用户文件相关属性保存到数据库，并返回跳过上传
        synchronized (FiletransferController.class) {
            //当数据库中有相同identifier，且pointCount>0时则走极速上传
            LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fileLambdaQueryWrapper.eq(File::getIdentifier,uploadFileDto.getIdentifier())
                    .isNotNull(File::getFileKey)
                    .gt(File::getPointCount,0);
            List<File> list = fileService.list(fileLambdaQueryWrapper);

            //List<File> list = fileService.listByMap(param);
            if (list != null && !list.isEmpty()) {
                File file = list.get(0);

                UserFile userfile = new UserFile();
                userfile.setFileId(file.getFileId());
                userfile.setUserId(sessionUser.getUserId());
                userfile.setFilePath(uploadFileDto.getFilePath());
                userfile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                userfile.setExtendName(FileUtil.getFileExtendName(fileName));
                userfile.setIsDir(0);
                userfile.setUploadTime(DateUtil.getCurrentTime());
                userfile.setDeleteFlag(0);
                userfileService.save(userfile);

                //每有一个人上传已存在的文件，将file的pointCount+1
                fileService.increaseFilePointCount(file);

                uploadFileVo.setSkipUpload(true);

            } else {
                uploadFileVo.setSkipUpload(false);

            }
        }
        return RestResult.success().data(uploadFileVo);
    }

    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    public RestResult<UploadFileVO> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        User sessionUser = userService.getUserByToken(token);
        if (sessionUser == null){
            return RestResult.fail().message("未登录");
        }

        int flag = 0;
        filetransferService.uploadFile(request, uploadFileDto, sessionUser.getUserId(),flag);
        UploadFileVO uploadFileVO = new UploadFileVO();
        return RestResult.success().data(uploadFileVO);

    }

    @Operation(summary = "加密上传文件", description = "真正的加密上传文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/encryptedupload", method = RequestMethod.POST)
    public RestResult<UploadFileVO> encryptedUpload(HttpServletRequest request, UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        User sessionUser = userService.getUserByToken(token);
        if (sessionUser == null){
            return RestResult.fail().message("未登录");
        }

        int flag = 1;
        filetransferService.uploadFile(request, uploadFileDto, sessionUser.getUserId(),flag);
        UploadFileVO uploadFileVO = new UploadFileVO();
        return RestResult.success().data(uploadFileVO);

    }


    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, DownloadFileDTO downloadFileDTO) throws RSA.pqException {

        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getUserFileId,downloadFileDTO.getUserFileId());
        UserFile userFile = userfileService.getOne(userFileLambdaQueryWrapper);
        Long fileId = userFile.getFileId();
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getFileId,fileId);
        File file = fileService.getOne(fileLambdaQueryWrapper);
        String fileKey = file.getFileKey();
        int flag = -1;
        if(fileKey == null) {
            flag = 0;
        }else{
            flag = 1;
        }
        filetransferService.downloadFile(response, downloadFileDTO,flag);
    }

    @Operation(summary = "获取存储信息", description = "获取存储信息", tags = {"filetransfer"})
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<Long> getStorage(@RequestHeader("token") String token) {

        User sessionUserBean = userService.getUserByToken(token);
//        Storage storageBean = new Storage();
        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        return RestResult.success().data(storageSize);

    }
}
