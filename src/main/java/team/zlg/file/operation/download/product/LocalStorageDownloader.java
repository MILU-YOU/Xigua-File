package team.zlg.file.operation.download.product;

import org.springframework.stereotype.Component;
import team.zlg.file.operation.download.Downloader;
import team.zlg.file.operation.download.domain.DownloadFile;
import team.zlg.file.util.PathUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Component
public class LocalStorageDownloader extends Downloader {
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        //设置文件路径
        File file = new File(PathUtil.getStaticPath() + downloadFile.getFileUrl());
        if (file.exists()) {

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = httpServletResponse.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
