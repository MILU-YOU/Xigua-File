package team.zlg.file.service.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import team.zlg.file.common.RestResult;
import team.zlg.file.mapper.UserMapper;
import team.zlg.file.model.User;
import team.zlg.file.service.UserService;
import team.zlg.file.util.DateUtil;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import team.zlg.file.util.JwtUtil;
import io.jsonwebtoken.Claims;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;
    @Resource
    JwtUtil jwtUtil;


    @Override
    public RestResult<String> registerUser(User user) {
        //判断验证码
        String telephone = user.getTelephone();
        String password = user.getPassword();

        if (!StringUtils.hasLength(telephone) || !StringUtils.hasLength(password)){
            return RestResult.fail().message("手机号或密码不能为空！");
        }
        if (isTelePhoneExit(telephone)){
            return RestResult.fail().message("手机号已存在！");
        }

        //随机盐值UUID
        String salt = UUID.randomUUID().toString().replace("-", "").substring(15);
        String passwordAndSalt = password + salt;
        // 密码=md5(随机盐值+密码)
        String newPassword = DigestUtils.md5DigestAsHex(passwordAndSalt.getBytes());

        user.setSalt(salt);

        user.setPassword(newPassword);
        user.setRegisterTime(DateUtil.getCurrentTime());
        int result = userMapper.insert(user);

        if (result == 1) {
            return RestResult.success();
        } else {
            return RestResult.fail().message("注册用户失败，请检查输入信息！");
        }
    }

    private boolean isTelePhoneExit(String telePhone) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getTelephone, telePhone);
        List<User> list = userMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public RestResult<User> login(User user) {
        String telephone = user.getTelephone();
        String password = user.getPassword();

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getTelephone, telephone);
        User saveUser = userMapper.selectOne(lambdaQueryWrapper);
        String salt = saveUser.getSalt();
        String passwordAndSalt = password + salt;
        String newPassword = DigestUtils.md5DigestAsHex(passwordAndSalt.getBytes());
        if (newPassword.equals(saveUser.getPassword())) {
            saveUser.setPassword("");//返回的视图对象LoginVO不需要这两个数据，只需要username
            saveUser.setSalt("");
            return RestResult.success().data(saveUser);
        } else {
            return RestResult.fail().message("手机号或密码错误！");
        }
    }

    @Override
    public User getUserByToken(String token) {
        User tokenUserInfo = null;
        try {

            Claims c = jwtUtil.parseJWT(token);
            String subject = c.getSubject();
            //Jackson objectMapper类，用于解析json为java对象
            ObjectMapper objectMapper = new ObjectMapper();
            //readValue()方法的第一个参数是JSON数据源(字符串, 流或者文件), 第二个参数是解析目标Java类
            tokenUserInfo = objectMapper.readValue(subject, User.class);

        } catch (Exception e) {
            log.error("解码异常");
            return null;

        }
        return tokenUserInfo;
    }

}