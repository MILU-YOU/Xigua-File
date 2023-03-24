package team.zlg.file.service.impl;

import org.springframework.stereotype.Service;
import team.zlg.file.dto.DownloadFileDTO;
import team.zlg.file.dto.UploadFileDTO;
import team.zlg.file.mapper.FileMapper;
import team.zlg.file.mapper.UserfileMapper;
import team.zlg.file.model.File;
import team.zlg.file.model.UserFile;
import team.zlg.file.operation.FileOperationFactory;
import team.zlg.file.operation.download.Downloader;
import team.zlg.file.operation.download.domain.DownloadFile;
import team.zlg.file.operation.upload.Uploader;
import team.zlg.file.operation.upload.domain.UploadFile;
import team.zlg.file.service.FiletransferService;
import team.zlg.file.util.DateUtil;
import team.zlg.file.util.PropertiesUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class FiletransferServiceImpl implements FiletransferService {

    @Resource
    FileMapper fileMapper;
    @Resource
    UserfileMapper userfileMapper;

    @Resource
    FileOperationFactory localStorageOperationFactory;

    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {

        //把前端传来的uploadFileDto的信息保存进uploadFile对象中
        Uploader uploader = null;
        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());
        //选择存储类型
        String storageType = PropertiesUtil.getProperty("file.storage-type");
        synchronized (FiletransferService.class) {
            //使用本地存储类型
            if ("0".equals(storageType)) {
                uploader = localStorageOperationFactory.getUploader();
            }
        }

        //调用上传方法
        List<UploadFile> uploadFileList = uploader.upload(request, uploadFile);
        for (int i = 0; i < uploadFileList.size(); i++){
            uploadFile = uploadFileList.get(i);
            File file = new File();

            file.setIdentifier(uploadFileDto.getIdentifier());
            file.setStorageType(Integer.parseInt(storageType));
            file.setTimeStampName(uploadFile.getTimeStampName());
            //如果上传成功，将上传的文件数据写入数据库中
            if (uploadFile.getSuccess() == 1){
                file.setFileUrl(uploadFile.getUrl());
                file.setFileSize(uploadFile.getFileSize());
                file.setPointCount(1);
                fileMapper.insert(file);
                UserFile userFile = new UserFile();
                userFile.setFileId(file.getFileId());
                userFile.setExtendName(uploadFile.getFileType());
                userFile.setFileName(uploadFile.getFileName());
                userFile.setFilePath(uploadFileDto.getFilePath());
                userFile.setDeleteFlag(0);
                userFile.setUserId(userId);
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userfileMapper.insert(userFile);
            }

        }
    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userfileMapper.selectById(downloadFileDTO.getUserFileId());

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名


        File file = fileMapper.selectById(userFile.getFileId());
        Downloader downloader = null;
        if (file.getStorageType() == 0) {
            downloader = localStorageOperationFactory.getDownloader();
        }
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(file.getFileUrl());
        downloadFile.setTimeStampName(file.getTimeStampName());
        downloader.download(httpServletResponse, downloadFile);
    }

    @Override
    public Long selectStorageSizeByUserId(Long userId) {
        return userfileMapper.selectStorageSizeByUserId(userId);
    }
}
