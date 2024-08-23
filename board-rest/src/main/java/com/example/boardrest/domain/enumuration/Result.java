package com.example.boardrest.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {

    SUCCESS("SUCCESS")
    , FAIL("FAIL")
    , ERROR("ERROR")
    , DUPLICATED("DUPLICATED")
    , AVAILABLE("AVAILABLE");

    private final String resultMessage;
}
