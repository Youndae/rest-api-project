package com.example.boardrest.auth.response;

import com.example.boardrest.domain.entity.Member;

public class OAuth2ResponseEntityConverter {

    public static Member toEntity(OAuth2Response oAuth2Response, String userId) {
        return Member.builder()
                        .userId(userId)
                        .email(oAuth2Response.getEmail())
                        .username(oAuth2Response.getName())
                        .provider(oAuth2Response.getProvider())
                        .build();
    }
}
