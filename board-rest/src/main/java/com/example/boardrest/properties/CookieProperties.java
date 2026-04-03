package com.example.boardrest.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookie")
@Getter
@Setter
public class CookieProperties {

    private Ino ino = new Ino();

    @Getter
    @Setter
    public static class Ino {
        private String header;
        private int age;
    }
}
