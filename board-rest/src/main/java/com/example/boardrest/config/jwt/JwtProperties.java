package com.example.boardrest.config.jwt;

public interface JwtProperties {

    String SECRET = "cocos";
    int ACCESS_EXPIRATION_TIME = 60000*10;
    String TOKEN_PREFIX = "Bearer";
    String ACCESS_HEADER_STRING = "Authorization";

    String REFRESH_HEADER_STRING = "Authorization_Refresh";

    int REFRESH_EXPIRATION_TIME = 14*24*60000*10;


}
