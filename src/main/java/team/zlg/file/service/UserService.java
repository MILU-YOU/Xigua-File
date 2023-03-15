package team.zlg.file.service;

import team.zlg.file.common.RestResult;
import team.zlg.file.model.User;

public interface UserService {
    RestResult<String> registerUser(User user);
    RestResult<User> login(User user);
    User getUserByToken(String token);
}