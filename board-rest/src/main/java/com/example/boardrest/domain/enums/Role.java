package com.example.boardrest.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {

    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN");

    private final String key;

    public static Role of(String key) {
        return Arrays.stream(Role.values())
                .filter(r -> r.getKey().equals(key))
                .findFirst()
                .orElse(MEMBER);
    }
}
