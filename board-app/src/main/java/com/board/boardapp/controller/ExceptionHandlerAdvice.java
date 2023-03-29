package com.board.boardapp.controller;

import com.board.boardapp.dto.CustomNotFoundException;
import com.board.boardapp.dto.ErrorCode;
import com.board.boardapp.dto.ErrorResponseEntity;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public String notFoundHandle(NotFoundException e){
        System.out.println("NotFountException : " + e);
        return "th/error/error";
    }


    @ExceptionHandler(NullPointerException.class)
    public String exceptionHandle(Exception e){
        log.info("NullPointerException e : " + e.getMessage());
        return "th/error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String AccessDeniedHandle(Exception e){
        log.info("AccessDeniedException e : " + e.getMessage());

        return "th/member/loginForm";
    }


    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<ErrorResponseEntity> customNotFoundExceptionResponseEntity(CustomNotFoundException e){

        log.info("CustomNotFoundException");
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }




}