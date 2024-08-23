package com.example.boardrest.auth.oAuth.domain;

import com.example.boardrest.domain.entity.Auth;
import com.example.boardrest.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2DTO {

    private String userId;

    private String username;

    private List<Auth> authList;

    private String nickname;

    public OAuth2DTO(Member existsData) {
        this.userId = existsData.getUserId();
        this.username = existsData.getUsername();
        this.authList = existsData.getAuths();
        this.nickname = existsData.getNickname();
    }
}
