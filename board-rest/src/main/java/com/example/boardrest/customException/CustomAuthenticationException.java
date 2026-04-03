package com.example.boardrest.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomAuthenticationException extends RuntimeException{

    ErrorCode errorCode;

    String message;
}
