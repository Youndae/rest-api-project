package com.example.boardrest.domain.enumuration;

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