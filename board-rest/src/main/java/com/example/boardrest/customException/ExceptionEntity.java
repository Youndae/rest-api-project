package com.example.boardrest.customException;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExceptionEntity {

    private String errorCode;

    private String errorMessage;

    @Builder
    public ExceptionEntity(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
