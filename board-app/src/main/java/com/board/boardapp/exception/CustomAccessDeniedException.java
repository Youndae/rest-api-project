package com.board.boardapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomAccessDeniedException extends RuntimeException{

    ErrorCode errorCode;

    String message;
}
