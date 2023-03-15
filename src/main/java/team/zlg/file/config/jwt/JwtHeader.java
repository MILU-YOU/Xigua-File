package team.zlg.file.config.jwt;

import lombok.Data;

@Data
public class JwtHeader {
    private String alg;
    private String typ;
}