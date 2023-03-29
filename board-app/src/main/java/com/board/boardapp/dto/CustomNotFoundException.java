package com.board.boardapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomNotFoundException extends RuntimeException {

    ErrorCode errorCode;
}
