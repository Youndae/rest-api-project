package com.board.boardapp.config.properties;

import java.time.Duration;

public interface JwtProperties {

    String ACCESS_HEADER_STRING = "Authorization";

    String REFRESH_HEADER_STRING = "Authorization_Refresh";

    String INO_HEADER_STRING = "Authorization_ino";

    int REFRESH_MAX_AGE = 60*60*24*14;

    int ACCESS_MAX_AGE = 60*59;

    int INO_MAX_AGE = 60*60*24*30;

    Duration REFRESH_AGE = Duration.ofDays(14);

    Duration ACCESS_AGE = Duration.ofHours(2);

    Duration INO_AGE = Duration.ofDays(99999);

    String LSC_HEADER_STRING = "lsc";

    String[] COOKIE_ARRAY = {ACCESS_HEADER_STRING, REFRESH_HEADER_STRING, INO_HEADER_STRING};


}
