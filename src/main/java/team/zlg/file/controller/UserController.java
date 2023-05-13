package team.zlg.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import team.zlg.file.common.RestResult;
import team.zlg.file.dto.RegisterDTO;
import team.zlg.file.model.User;
import team.zlg.file.service.UserService;
import team.zlg.file.util.DateUtil;
import team.zlg.file.util.JwtUtil;
import team.zlg.file.util.SMSUtils;
import team.zlg.file.util.ValidateCodeUtils;
import team.zlg.file.vo.LoginVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "user",description = "该接口为用户接口，主要做用户登录，注册和校验token")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public RestResult<String> sendMsg(@RequestBody RegisterDTO registerDTO){

        String telephone = registerDTO.getTelephone();
        if(StringUtils.hasText(telephone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            SMSUtils.sendMessage("西瓜网盘","SMS_276395681",telephone,code);

            //需要将生成的验证码保存到Session
//            session.setAttribute(telephone,code);

            //将生成的验证码保存到redis中
            redisTemplate.opsForValue().set(telephone,code,5, TimeUnit.MINUTES);

            return RestResult.success().message("手机验证码短信发送成功");

        }
        //session.setAttribute("user",user.getId());
        return RestResult.fail().message("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public RestResult<LoginVO> login(@RequestBody Map map){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);

        //从redis中获取保存的验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对(页面提交的验证码和Session中保存的验证码比对)
        if(codeInRedis != null&& codeInRedis.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getTelephone,phone);

            User user = userService.getOne(queryWrapper);
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            if(user == null){
                user = new User();
                user.setUsername("user"+phone);
                user.setTelephone(phone);
                user.setRegisterTime(DateUtil.getCurrentTime());
                userService.save(user);
            }
            //session.setAttribute("user",user.getId());

            //如果用户登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            LoginVO loginVO = new LoginVO();
            loginVO.setUsername(user.getUsername());
            String jwt = "";
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                jwt = jwtUtil.createJWT(objectMapper.writeValueAsString(user));
            } catch (Exception e) {
                return RestResult.fail().message("登录失败！");
            }
            loginVO.setToken(jwt);

            return RestResult.success().data(loginVO);
        }
        return RestResult.fail().message("登陆失败");
    }


    /**
     *用户注册
     * @param registerDTO
     * @return
     */
    @Operation(summary = "用户注册", description = "注册账号", tags = {"user"})
    @PostMapping(value = "/register")
    public RestResult<String> register(@RequestBody RegisterDTO registerDTO) {
        RestResult<String> restResult = null;
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setTelephone(registerDTO.getTelephone());
        user.setPassword(registerDTO.getPassword());

        restResult = userService.registerUser(user);

        return restResult;
    }

    /**
     * 用户登录
     * @param telephone
     * @param password
     * @return
     */
    @GetMapping(value = "/login")
    @Operation(summary = "用户登录", description = "用户登录认证后才能进入系统", tags = {"user"})
    public RestResult<LoginVO> userLogin(String telephone, String password) {
        RestResult<LoginVO> restResult = new RestResult<LoginVO>();
        LoginVO loginVO = new LoginVO();
        User user = new User();
        user.setTelephone(telephone);
        user.setPassword(password);
        RestResult<User> loginResult = userService.login(user);

        if (!loginResult.getSuccess()) {
            return RestResult.fail().message("登录失败！");
        }

        loginVO.setUsername(loginResult.getData().getUsername());
        String jwt = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jwt = jwtUtil.createJWT(objectMapper.writeValueAsString(loginResult.getData()));
        } catch (Exception e) {
            return RestResult.fail().message("登录失败！");
        }
        loginVO.setToken(jwt);

        return RestResult.success().data(loginVO);
    }

    /**
     * token校验
     * @param token
     * @return
     */
    @GetMapping("/checkuserlogininfo")
    @Operation(summary = "检查用户登录信息", description = "验证token的有效性", tags = {"user"})
    public RestResult<User> checkToken(@RequestHeader("token") String token) {
        RestResult<User> restResult = new RestResult<User>();
        User tokenUserInfo = null;
        try {

            Claims c = jwtUtil.parseJWT(token);
            String subject = c.getSubject();
            ObjectMapper objectMapper = new ObjectMapper();
            tokenUserInfo = objectMapper.readValue(subject, User.class);

        } catch (Exception e) {
            log.error("解码异常");
            return RestResult.fail().message("认证失败");

        }

        if (tokenUserInfo != null) {

            return RestResult.success().data(tokenUserInfo);

        } else {
            return RestResult.fail().message("用户暂未登录");
        }
    }
}
