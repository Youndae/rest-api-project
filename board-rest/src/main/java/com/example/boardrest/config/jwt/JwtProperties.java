package com.example.boardrest.config.jwt;

import java.time.Duration;

public interface JwtProperties {

    String SECRET = "cocos";
    long ACCESS_TOKEN_EXPIRATION_TIME = 60000 * 60;

    long ACCESS_COOKIE_MAX_AGE = 60 * 2;

    String TOKEN_PREFIX = "Bearer";
    String ACCESS_HEADER_STRING = "Authorization";

    String REFRESH_HEADER_STRING = "Authorization_Refresh";

    String INO_HEADER_STRING = "Authorization_ino";

    long REFRESH_TOKEN_EXPIRATION_TIME = 60000 * 60 * 24 * 14;

    long REFRESH_COOKIE_MAX_AGE = 60 * 60 * 24 * 14;

    String ACCESS_TOKEN_PREFIX = "at";

    String REFRESH_TOKEN_PREFIX = "rt";

    long INO_COOKIE_MAX_AGE = 60 * 60 * 24 * 30;
}
