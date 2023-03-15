package team.zlg.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import team.zlg.file.util.PropertiesUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class PropertiesConfig {

    @Resource
    private Environment env;

    @PostConstruct
    public void setProperties() {
        PropertiesUtil.setEnvironment(env);
    }
}
