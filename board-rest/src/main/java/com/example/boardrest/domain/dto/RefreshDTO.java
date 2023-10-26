package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;

@Deprecated
/**
 * Redis 적용으로 인해 RefreshToken 관리를 DB -> Redis로 넘기게 되어
 * DTO 사용 안함.
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RefreshDTO {

    private String refreshIndex;

    private String tokenVal;

    private Date tokenExpires;

    private String originIndex;


}
