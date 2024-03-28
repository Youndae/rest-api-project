package com.example.boardrest.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    TOKEN_STEALING(800, "TokenStealingException");

    private final Integer httpStatus;

    private final String message;
}
