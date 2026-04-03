package com.example.boardrest.customException;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID"),
    TOKEN_STEALING(HttpStatus.UNAUTHORIZED, "TOKEN_STEALING"),
    ORDER_SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "ORDER_SESSION_EXPIRED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),
    ORDER_DATA_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ORDER_DATA_FAILED"),
    DB_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB_CONNECTION_ERROR"),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "BAD_GATEWAY");

    private final HttpStatus httpStatus;

    @JsonValue
    private final String message;
}
