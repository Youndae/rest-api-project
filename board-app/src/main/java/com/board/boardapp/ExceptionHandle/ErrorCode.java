package com.board.boardapp.ExceptionHandle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    USER_NOT_FOUND(900, "로그인 정보 오류")
    , REISSUED_ERROR(600, "토큰 재발급 오류")
    , TOKEN_STEALING(800, "토큰 탈취 오류")
    , ACCESS_DENIED(403, "권한 오류");



    private final Integer httpStatus;

    private final String message;

}
