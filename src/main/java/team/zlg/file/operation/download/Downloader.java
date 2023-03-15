package team.zlg.file.operation.download;

import team.zlg.file.operation.download.domain.DownloadFile;

import javax.servlet.http.HttpServletResponse;

public abstract class Downloader {
    public abstract void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile);
}
