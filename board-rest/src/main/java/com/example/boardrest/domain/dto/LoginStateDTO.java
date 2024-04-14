package com.example.boardrest.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginStateDTO {
    private boolean loginStatus;

    public LoginStateDTO() {
        this.loginStatus = false;
    }

    public void setStatusValueForTrue(){
        this.loginStatus = true;
    }
}
