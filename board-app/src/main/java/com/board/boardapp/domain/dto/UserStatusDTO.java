package com.board.boardapp.domain.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {

    private boolean loggedIn;

    private String uid;

    public UserStatusDTO(boolean loggedIn){
        this.loggedIn = loggedIn;
    }
}
