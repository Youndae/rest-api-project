package com.example.boardrest.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class TokenProperties {

    private String prefix;
    private Access access = new Access();
    private Refresh refresh = new Refresh();

    @Getter
    @Setter
    public static class Access {
        private String header;
        private long expiration;
    }

    @Getter
    @Setter
    public static class Refresh {
        private String header;
        private long expiration;
    }
}
