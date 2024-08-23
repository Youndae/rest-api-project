package com.example.boardrest.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    TOKEN_STEALING(800, "TokenStealingException")
    , ACCESS_DENIED(403, "AccessDeniedException")
    , IO_EXCEPTION(400, "IOException");

    private final Integer httpStatus;

    private final String message;
}
