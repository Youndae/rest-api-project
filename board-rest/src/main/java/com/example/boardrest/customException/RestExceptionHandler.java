package com.example.boardrest.customException;

import com.example.boardrest.domain.dto.exception.ValidationError;
import com.example.boardrest.domain.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.debug("HandleMethodArgumentNotValidException::message : {}", e.getMessage());
        log.debug("HandleMethodArgumentNotValidException::AllErrors : {}", e.getAllErrors());



        List<ValidationError> errors = e.getFieldErrors()
                .stream()
                .map(err -> new ValidationError(
                        err.getField(),
                        err.getCode(),
                        err.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(
                        ExceptionResponse.validationException(ErrorCode.BAD_REQUEST, errors)
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        exceptionLog(e);

        return ResponseEntity.badRequest()
                .body(
                        ExceptionResponse.exception(ErrorCode.BAD_REQUEST)
                );
    }



    @ExceptionHandler({NullPointerException.class, CustomIOException.class})
    public ResponseEntity<ExceptionResponse<Void>> nullPointerExceptionHandler(Exception e){

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.exception(ErrorCode.INTERNAL_SERVER_ERROR)
                );
    }

    @ExceptionHandler({AccessDeniedException.class, CustomAccessDeniedException.class, BadCredentialsException.class})
    public ResponseEntity<ExceptionResponse<Void>> accessDeniedExceptionHandler(Exception e) {

        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ExceptionResponse.exception(ErrorCode.FORBIDDEN)
                );
    }

    @ExceptionHandler({
            FileNotFoundException.class,
            CustomNotFoundException.class,
            NoSuchFileException.class,
            IllegalArgumentException.class,
            IllegalAccessException.class,
            CustomInvalidJoinPolicyException.class
    })
    public ResponseEntity<ExceptionResponse<Void>> fileNotFoundExceptionHandler(Exception e){
        exceptionLog(e);

        return ResponseEntity.badRequest().body(ExceptionResponse.exception(ErrorCode.BAD_REQUEST, "잘못된 요청입니다."));
    }

    @ExceptionHandler({
            CannotCreateTransactionException.class,
            JpaSystemException.class,
            SQLException.class
    })
    public ResponseEntity<ExceptionResponse<Void>> dbSystemException(Exception e) {
        exceptionLog(e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.exception(ErrorCode.INTERNAL_SERVER_ERROR)
                );
    }

    public void exceptionLog(Exception e){

        log.error(e.toString());

        StackTraceElement[] trace = e.getStackTrace();

        for(StackTraceElement a : trace)
            log.error(a.toString());
    }
}
