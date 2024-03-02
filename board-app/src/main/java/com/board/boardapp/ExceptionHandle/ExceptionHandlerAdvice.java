package com.board.boardapp.ExceptionHandle;

import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorResponseEntity;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public String notFoundHandle(NotFoundException e){
        log.info("NotFountException : " + e);
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

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity sizeLimitExceededExceptionHandler(SizeLimitExceededException e){

        log.info("size limit exception");

        Map<String, String> result = new HashMap<>();
        result.put("message", "Large File");

        return ResponseEntity.status(HttpStatus.valueOf(400))
                .body(result);
    }


}
