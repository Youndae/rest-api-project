package com.board.boardapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomNotFoundException extends RuntimeException {

    ErrorCode errorCode;
}
