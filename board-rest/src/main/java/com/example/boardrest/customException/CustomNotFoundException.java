package com.example.boardrest.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomNotFoundException extends RuntimeException{

    ErrorCode errorCode;

    String message;
}
