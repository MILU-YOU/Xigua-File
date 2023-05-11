package team.zlg.file.operation.download.product;

import org.springframework.stereotype.Component;
import team.zlg.file.operation.download.Downloader;
import team.zlg.file.operation.download.domain.DownloadFile;
import team.zlg.file.util.AES_128;
import team.zlg.file.util.PathUtil;
import team.zlg.file.util.RSA;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalStorageDownloader extends Downloader {
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile,int flag) throws RSA.pqException {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];

        if(flag == 1) {
            //得到解密文件
            String s = PathUtil.getStaticPath() + downloadFile.getFileUrl();
            StringBuffer stringBuffer = new StringBuffer(s);
            int index = stringBuffer.lastIndexOf(".");
            stringBuffer.insert(index, "_temp");
            String tempFilePath = stringBuffer.toString();
            //得到加密的密钥key
            String key1 = downloadFile.getKey();
            //解密密钥key
            BigInteger bigInteger = new BigInteger(key1);
            BigInteger[] key = new BigInteger[1];
            key[0] = bigInteger;
            RSA rsa = new RSA(new BigInteger("65537"), 0, 1024);
            String decryptionKey = rsa.decryption(key);
            //解密文件
            AES_128 aes = new AES_128(decryptionKey, downloadFile.getIv());
            aes.decrypted(PathUtil.getStaticPath() + downloadFile.getFileUrl(), tempFilePath);

            //下载文件
            File file = new File(tempFilePath);
            if (file.exists()) {

                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = httpServletResponse.getOutputStream();
                    //从输入流中读取一定数量的字节，并将这些字节存储到具有缓冲作用的数组b中，返回一次性读取的字节数
                    int i = bis.read(buffer);
                    while (i != -1) {
                        //向输出流中写入buffer缓冲字节数组中0到i个的内容
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                            //删除解密的文件
                            Path path = Paths.get(tempFilePath);
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else if(flag == 0){
            //设置文件路径
            File file = new File(PathUtil.getStaticPath() + downloadFile.getFileUrl());
            if (file.exists()) {

                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = httpServletResponse.getOutputStream();
                    //从输入流中读取一定数量的字节，并将这些字节存储到具有缓冲作用的数组b中，返回一次性读取的字节数
                    int i = bis.read(buffer);
                    while (i != -1) {
                        //向输出流中写入buffer缓冲字节数组中0到i个的内容
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
}
