package com.example.boardrest.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {

    LOCAL("lcoal"),
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private final String key;
}
