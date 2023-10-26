package com.board.boardapp.dto;

public interface JwtProperties {

    String ACCESS_HEADER_STRING = "Authorization";

    String REFRESH_HEADER_STRING = "Authorization_Refresh";

    String INO_HEADER_STRING = "Authorization_ino";

    int REFRESH_MAX_AGE = 60*60*24*14;

    int ACCESS_MAX_AGE = 60*59;

    String LSC_HEADER_STRING = "lsc";


}
