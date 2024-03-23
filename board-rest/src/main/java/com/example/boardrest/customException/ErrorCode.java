package com.example.boardrest.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    TOKEN_STEALING(800, "토큰 탈취 오류");

    private final Integer httpStatus;

    private final String message;
}
