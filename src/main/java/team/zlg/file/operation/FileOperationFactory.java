package team.zlg.file.operation;

import team.zlg.file.operation.delete.Deleter;
import team.zlg.file.operation.download.Downloader;
import team.zlg.file.operation.upload.Uploader;

public interface FileOperationFactory {
    Uploader getUploader();
    Downloader getDownloader();
    Deleter getDeleter();
}
