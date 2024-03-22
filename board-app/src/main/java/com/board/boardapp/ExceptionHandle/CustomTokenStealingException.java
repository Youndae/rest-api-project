package com.board.boardapp.ExceptionHandle;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 토큰이 탈취되었다는 응답이 오는 경우 발생시킬 Exception
 */
@AllArgsConstructor
@Getter
public class CustomTokenStealingException extends RuntimeException{

    ErrorCode errorCode;

}
