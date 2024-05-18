package com.board.boardapp.dto;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinDTO {

    private String userId;

    private String userPw;

    private String userName;

    private String nickname;

    private String email;

}
