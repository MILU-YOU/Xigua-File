package team.zlg.file.operation;

import org.springframework.stereotype.Component;
import team.zlg.file.operation.delete.Deleter;
import team.zlg.file.operation.delete.product.LocalStorageDeleter;
import team.zlg.file.operation.download.Downloader;
import team.zlg.file.operation.download.product.LocalStorageDownloader;
import team.zlg.file.operation.upload.Uploader;
import team.zlg.file.operation.upload.product.LocalStorageUploader;

import javax.annotation.Resource;

@Component
public class LocalStorageOperationFactory implements FileOperationFactory{

    @Resource
    LocalStorageUploader localStorageUploader;
    @Resource
    LocalStorageDownloader localStorageDownloader;
    @Resource
    LocalStorageDeleter localStorageDeleter;
    @Override
    public Uploader getUploader() {
        return localStorageUploader;
    }

    @Override
    public Downloader getDownloader() {
        return localStorageDownloader;
    }

    @Override
    public Deleter getDeleter() {
        return localStorageDeleter;
    }


}
