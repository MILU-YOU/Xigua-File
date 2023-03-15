package team.zlg.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import team.zlg.file.common.RestResult;
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
import team.zlg.file.vo.UploadFileVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @GetMapping(value = "/uploadfile")
    public RestResult<UploadFileVO> uploadFileSpeed(UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        User sessionUser = userService.getUserByToken(token);
        if (sessionUser == null){

            return RestResult.fail().message("未登录");
        }

        UploadFileVO uploadFileVo = new UploadFileVO();
        Map<String, Object> param = new HashMap<String, Object>();
        //查询数据库中是否有与上传文件相同的identifier，如果是则直接上传成功，设置用户文件相关属性保存到数据库，并返回跳过上传
        param.put("identifier", uploadFileDto.getIdentifier()); 
        synchronized (FiletransferController.class) {
            List<File> list = fileService.listByMap(param);
            if (list != null && !list.isEmpty()) {
                File file = list.get(0);

                UserFile userfile = new UserFile();
                userfile.setFileId(file.getFileId());
                userfile.setUserId(sessionUser.getUserId());
                userfile.setFilePath(uploadFileDto.getFilePath());
                String fileName = uploadFileDto.getFilename();
                userfile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                userfile.setExtendName(FileUtil.getFileExtendName(fileName));
                userfile.setIsDir(0);
                userfile.setUploadTime(DateUtil.getCurrentTime());
                userfile.setDeleteFlag(0);
                userfileService.save(userfile);
                // fileService.increaseFilePointCount(file.getFileId());
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


        filetransferService.uploadFile(request, uploadFileDto, sessionUser.getUserId());
        UploadFileVO uploadFileVO = new UploadFileVO();
        return RestResult.success().data(uploadFileVO);

    }

    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, DownloadFileDTO downloadFileDTO) {
        filetransferService.downloadFile(response, downloadFileDTO);
    }

    @Operation(summary = "获取存储信息", description = "获取存储信息", tags = {"filetransfer"})
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<Long> getStorage(@RequestHeader("token") String token) {

        User sessionUserBean = userService.getUserByToken(token);
        Storage storageBean = new Storage();


        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        return RestResult.success().data(storageSize);

    }
}
