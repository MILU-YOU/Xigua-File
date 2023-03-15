package team.zlg.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import team.zlg.file.model.RecoveryFile;
import team.zlg.file.vo.RecoveryFileListVO;

import java.util.List;

public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    List<RecoveryFileListVO> selectRecoveryFileList();
}
