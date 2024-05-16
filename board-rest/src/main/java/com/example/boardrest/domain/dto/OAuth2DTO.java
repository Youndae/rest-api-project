package com.example.boardrest.domain.dto;

import com.example.boardrest.domain.entity.Auth;
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
}
