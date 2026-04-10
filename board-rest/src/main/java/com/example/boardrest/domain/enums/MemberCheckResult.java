package com.example.boardrest.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberCheckResult {
    VALID("VALID"),
    INVALID("INVALID"),
    DUPLICATED("DUPLICATED");

    @JsonValue
    private final String message;
}
