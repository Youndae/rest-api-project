package com.example.boardrest.domain.dto.member.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Getter
@NoArgsConstructor
public class LoginStateDTO {
    private boolean loginStatus;

    public LoginStateDTO(Principal principal) {
        this.loginStatus = principal != null;
    }
}
