package team.zlg.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.zlg.file.mapper.FileMapper;
import team.zlg.file.model.File;
import team.zlg.file.service.FileService;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {


}