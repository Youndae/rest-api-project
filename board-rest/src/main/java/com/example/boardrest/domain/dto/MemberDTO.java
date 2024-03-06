package com.example.boardrest.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class MemberDTO {
//
    private String userId;

    private String userPw;

    private String userName;

}
