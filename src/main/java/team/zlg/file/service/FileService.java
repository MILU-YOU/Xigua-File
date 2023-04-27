package team.zlg.file.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import team.zlg.file.model.File;

import javax.annotation.Resource;

public interface FileService extends IService<File> {
    void increaseFilePointCount(File file);
}