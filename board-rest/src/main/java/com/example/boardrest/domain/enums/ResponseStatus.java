package com.example.boardrest.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {
    SUCCESS("success"),
    FAIL("fail"),
    ERROR("error");

    private final String message;
}