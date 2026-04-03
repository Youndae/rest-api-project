package com.example.boardrest.domain.dto.response;

import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.enumuration.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExceptionResponse <T> {
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorCode errorCode;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<T> errors;

    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public static <T> ExceptionResponse<T> exception(ErrorCode errorCode) {
        return ExceptionResponse.<T>builder()
                .code(errorCode.getHttpStatus().value())
                .message(ResponseStatus.FAIL.getMessage())
                .build();
    }

    public static <T> ExceptionResponse<T> exception(ErrorCode errorCode, String message) {
        return ExceptionResponse.<T>builder()
                .code(errorCode.getHttpStatus().value())
                .message(message)
                .build();
    }

    public static <T> ExceptionResponse<T> validationException(ErrorCode errorCode, List<T> errors) {
        return ExceptionResponse.<T>builder()
                .code(errorCode.getHttpStatus().value())
                .message(ResponseStatus.FAIL.getMessage())
                .errors(errors)
                .build();
    }

    public static <T> ExceptionResponse<T> validationException(ErrorCode errorCode, List<T> errors, String message) {
        return ExceptionResponse.<T>builder()
                .code(errorCode.getHttpStatus().value())
                .message(message)
                .errors(errors)
                .build();
    }
}
