package com.example.boardrest.domain.dto;

import lombok.*;

import java.security.Principal;

/**
 * 로그인 여부와 사용자 아이디를 반환하는 DTO
 */

@Getter
@ToString
@NoArgsConstructor
public class UserStatusDTO {

    private boolean loggedIn;

    private String uid;

    public UserStatusDTO(Principal principal) {

        if(principal == null){
            this.loggedIn = false;
            this.uid = null;
        }else{
            this.loggedIn = true;
            this.uid = principal.getName();
        }

    }
}
