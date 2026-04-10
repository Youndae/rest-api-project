package com.example.boardrest.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenValidationResult {

    TOKEN_STEALING("TOKEN_STEALING"),
    TOKEN_EXPIRATION("TOKEN_EXPIRATION"),
    WRONG_TOKEN("INVALID_TOKEN");

    private final String result;
}
