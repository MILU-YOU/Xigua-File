package team.zlg.file;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.util.ResourceUtils;
import team.zlg.file.util.PathUtil;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
@MapperScan("team.zlg.file.mapper")
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws FileNotFoundException {
        SpringApplication.run(Application.class, args);
        log.info("项目启动成功");
    }

}