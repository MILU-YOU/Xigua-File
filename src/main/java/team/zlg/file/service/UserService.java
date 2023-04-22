package team.zlg.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import team.zlg.file.common.RestResult;
import team.zlg.file.model.User;

public interface UserService extends IService<User> {
    RestResult<String> registerUser(User user);
    RestResult<User> login(User user);
    User getUserByToken(String token);
}