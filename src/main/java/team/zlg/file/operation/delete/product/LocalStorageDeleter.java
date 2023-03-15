package team.zlg.file.operation.delete.product;

import org.springframework.stereotype.Component;
import team.zlg.file.operation.delete.Deleter;
import team.zlg.file.operation.delete.domain.DeleteFile;
import team.zlg.file.util.FileUtil;
import team.zlg.file.util.PathUtil;

import java.io.File;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File file = new File(PathUtil.getStaticPath() + deleteFile.getFileUrl());
        if (file.exists()) {
            file.delete();
        }

        if (FileUtil.isImageFile(FileUtil.getFileExtendName(deleteFile.getFileUrl()))) {
            File minFile = new File(PathUtil.getStaticPath() + deleteFile.getFileUrl().replace(deleteFile.getTimeStampName(), deleteFile.getTimeStampName() + "_min"));
            if (minFile.exists()) {
                minFile.delete();
            }
        }
    }
}
