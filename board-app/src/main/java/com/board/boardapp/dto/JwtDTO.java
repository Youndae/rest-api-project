package com.board.boardapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JwtDTO {

    private String accessTokenHeader;

    private String accessTokenValue;

    private String refreshTokenHeader;

    private String refreshTokenValue;
}
