package com.example.boardrest.config.jwt;

import java.time.Duration;

public interface JwtProperties {

    String ACCESS_SECRET = "cocoAccess";

    String REFRESH_SECRET = "cocoRefresh";
    long ACCESS_TOKEN_EXPIRATION_TIME = 60000 * 60;

//    long ACCESS_TOKEN_EXPIRATION_TIME = 1000;
    String TOKEN_PREFIX = "Bearer";
    String ACCESS_HEADER_STRING = "Authorization";

    String REFRESH_HEADER_STRING = "Authorization_Refresh";

    String INO_HEADER_STRING = "Authorization_ino";

    long REFRESH_TOKEN_EXPIRATION_TIME = 60000 * 60 * 24 * 14;

    Duration REDIS_TOKEN_DATA_EXPIRATION_TIME = Duration.ofDays(30L);

    String ACCESS_TOKEN_PREFIX = "at";

    String REFRESH_TOKEN_PREFIX = "rt";

    long INO_COOKIE_MAX_AGE = 60 * 60 * 24 * 9999;

    //all token cookie's max age
    long TOKEN_COOKIE_MAX_AGE = 60 * 60 * 24 * 30;

    String TOKEN_STEALING_RESULT = "st";

    String TOKEN_EXPIRATION_RESULT = "expiration_token";

    String WRONG_TOKEN = "wrong_token";
}
