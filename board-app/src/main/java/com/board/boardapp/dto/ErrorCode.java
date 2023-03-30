package com.board.boardapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    USER_NOT_FOUND(900, "로그인 정보 오류");



    private final Integer httpStatus;

    private final String message;

}
