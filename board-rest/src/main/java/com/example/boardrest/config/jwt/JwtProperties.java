package com.example.boardrest.config.jwt;

import java.time.Duration;

public interface JwtProperties {

    String TOKEN_PREFIX = "Bearer";
    String ACCESS_HEADER_STRING = "Authorization";
    String REFRESH_HEADER_STRING = "Authorization_Refresh";
    String INO_HEADER_STRING = "Authorization_ino";

    String TOKEN_STEALING_RESULT = "st";

    String TOKEN_EXPIRATION_RESULT = "expiration_token";

    String WRONG_TOKEN = "wrong_token";
}
