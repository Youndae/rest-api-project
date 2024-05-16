package com.example.boardrest.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.CustomTokenStealingException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.customException.ExceptionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity tokenExpiredException(Exception e) {
        System.out.println("handling token expiredException");

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ExceptionEntity.builder()
                                .errorCode(String.valueOf(ErrorCode.TOKEN_STEALING.getHttpStatus()))
                                .errorMessage(ErrorCode.TOKEN_STEALING.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(CustomTokenStealingException.class)
    public ResponseEntity<ExceptionEntity> tokenStealingExceptionHandler(Exception e){
        exceptionLog(e);

        //response 를 통한 모든 쿠키 삭제.

        return ResponseEntity.status(800)
                .body(
                        ExceptionEntity.builder()
                                .errorCode(String.valueOf(ErrorCode.TOKEN_STEALING.getHttpStatus()))
                                .errorMessage(ErrorCode.TOKEN_STEALING.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity nullPointerExceptionHandler(Exception e){

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.valueOf(500)).build();
    }

    @ExceptionHandler({AccessDeniedException.class, CustomAccessDeniedException.class})
    public ResponseEntity<ExceptionEntity> accessDeniedExceptionHandler(Exception e) {

        log.info("AccessDenied Handle");

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ExceptionEntity.builder()
                                .errorCode(String.valueOf(ErrorCode.ACCESS_DENIED.getHttpStatus()))
                                .errorMessage("AccessDeniedException")
                                .build()
                );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity fileNotFoundExceptionHandler(Exception e){

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.valueOf(400)).build();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalAccessException.class})
    public ResponseEntity illegalExceptionHandler(Exception e){

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.valueOf(400)).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity badCredentialsExceptionHandler(Exception e){
        exceptionLog(e);

        System.out.println("BadCredentialsException");

        return ResponseEntity.status(HttpStatus.valueOf(403)).build();
    }

    public void exceptionLog(Exception e){

        log.error(e.toString());

        StackTraceElement[] trace = e.getStackTrace();

        for(StackTraceElement a : trace)
            log.error(a.toString());
    }
}
