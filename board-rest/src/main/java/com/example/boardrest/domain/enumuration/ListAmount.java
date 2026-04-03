package com.example.boardrest.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ListAmount {
    BOARD(20),
    IMAGE_BOARD(15),
    COMMENT(20);

    private final int amount;
}
