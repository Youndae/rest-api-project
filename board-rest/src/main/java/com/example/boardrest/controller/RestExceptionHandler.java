package com.example.boardrest.controller;

import com.example.boardrest.customException.CustomTokenStealingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomTokenStealingException.class)
    public ResponseEntity tokenStealingExceptionHandler(Exception e){
        exceptionLog(e);

        //response 를 통한 모든 쿠키 삭제.

        return ResponseEntity.status(HttpStatus.valueOf(800)).build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity nullPointerExceptionHandler(Exception e){

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.valueOf(500)).build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedExceptionHandler(Exception e) {

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.valueOf(403)).build();
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

        return ResponseEntity.status(HttpStatus.valueOf(403)).build();
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("handleMethodArgumentNotValid");
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    public void exceptionLog(Exception e){

        log.error(e.toString());

        StackTraceElement[] trace = e.getStackTrace();

        for(StackTraceElement a : trace)
            log.error(a.toString());

    }
}
