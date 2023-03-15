package team.zlg.file.service;

import team.zlg.file.dto.DownloadFileDTO;
import team.zlg.file.dto.UploadFileDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FiletransferService {
    void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId);
    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);
    Long selectStorageSizeByUserId(Long userId);
}
