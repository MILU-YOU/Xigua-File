package team.zlg.file.service;

import team.zlg.file.dto.DownloadFileDTO;
import team.zlg.file.dto.UploadFileDTO;
import team.zlg.file.util.RSA;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FiletransferService {
    void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId,int flag);
    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO,int flag) throws RSA.pqException;
    Long selectStorageSizeByUserId(Long userId);
}
