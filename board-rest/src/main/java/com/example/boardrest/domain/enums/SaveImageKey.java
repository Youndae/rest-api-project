package com.example.boardrest.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SaveImageKey {
    SAVE_NAME("imageName"),
    ORIGIN_NAME("originName");

    private final String value;
}
