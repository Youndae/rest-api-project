package com.example.boardrest.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomInvalidJoinPolicyException extends RuntimeException{

    ErrorCode errorCode;

    String message;
}
