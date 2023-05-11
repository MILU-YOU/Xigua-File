package team.zlg.file.operation.download;

import team.zlg.file.operation.download.domain.DownloadFile;
import team.zlg.file.util.RSA;

import javax.servlet.http.HttpServletResponse;

public abstract class Downloader {
    public abstract void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile,int flag) throws RSA.pqException;
}
