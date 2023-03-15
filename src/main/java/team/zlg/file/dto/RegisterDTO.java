package team.zlg.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//DTO代表服务层需要接收的数据和返回的数据，而VO代表展示层需要显示的数据。
@Schema(description="注册DTO")
@Data
public class RegisterDTO {
    private String username;
    private String telephone;
    private String password;
}