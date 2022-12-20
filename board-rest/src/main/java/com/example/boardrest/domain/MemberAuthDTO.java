package com.example.boardrest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberAuthDTO {

    private String userId;

    private String userPw;

    private Set<Auth> auth;
}
