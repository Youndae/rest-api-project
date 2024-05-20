package com.board.boardapp.ExceptionHandle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomBadCredentialsException extends RuntimeException{

    ErrorCode errorCode;

    String message;
}
