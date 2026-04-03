package com.example.boardrest.config;

import com.example.boardrest.properties.*;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableConfigurationProperties({
        CookieProperties.class,
        JwtSecretProperties.class,
        RedisProperties.class,
        TokenProperties.class,
        TokenRedisProperties.class
})
public class PropertiesConfig {

    @Bean(name = "filePath")
    public PropertiesFactoryBean filePathPropertiesFactoryBean() {
        String filePathPropertiesPath = "dev-filepath.properties";

        return setPropertiesFactoryBean(filePathPropertiesPath);
    }

    private PropertiesFactoryBean setPropertiesFactoryBean(String path) {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource(path);

        propertiesFactoryBean.setLocation(classPathResource);

        return propertiesFactoryBean;
    }
}
