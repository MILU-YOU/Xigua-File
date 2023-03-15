package team.zlg.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import team.zlg.file.model.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
/*    void insertUser(User user);
    List<User> selectUser();*/
}
