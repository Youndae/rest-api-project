package com.example.boardrest.domain.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Deprecated
public class JwtDTO {
//
    private String accessTokenHeader;

    private String accessTokenValue;

    private String refreshTokenHeader;

    private String refreshTokenValue;

    private String inoHeader;

    private String inoValue;
}
