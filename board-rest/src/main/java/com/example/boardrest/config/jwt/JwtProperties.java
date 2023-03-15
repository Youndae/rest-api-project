package com.example.boardrest.config.jwt;

import java.time.Duration;

public interface JwtProperties {

    String SECRET = "cocos";
    long ACCESS_EXPIRATION_TIME = 60000 * 60;

    String TOKEN_PREFIX = "Bearer";
    String ACCESS_HEADER_STRING = "Authorization";

    String REFRESH_HEADER_STRING = "Authorization_Refresh";

    long REFRESH_EXPIRATION_TIME = 60000 * 60 * 24 * 14;


}
