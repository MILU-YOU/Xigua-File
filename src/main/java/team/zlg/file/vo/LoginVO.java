package team.zlg.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="登录VO")
@Data
public class LoginVO {
    private String username;
    private String token;
}