package team.zlg.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.zlg.file.mapper.FileMapper;
import team.zlg.file.model.File;
import team.zlg.file.service.FileService;

import javax.annotation.Resource;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Resource
    FileMapper fileMapper;

    @Override
    public void increaseFilePointCount(File file) {
        int pointCount = file.getPointCount() + 1;
        LambdaUpdateWrapper<File> fileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        fileLambdaUpdateWrapper.set(File::getPointCount,pointCount)
                .eq(File::getFileId,file.getFileId());
        fileMapper.update(null, fileLambdaUpdateWrapper);
    }
}