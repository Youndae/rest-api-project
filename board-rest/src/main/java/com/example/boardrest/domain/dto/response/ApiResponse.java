package com.example.boardrest.domain.dto.response;

import com.example.boardrest.domain.enumuration.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApiResponse <T> {
    private int code;
    private String message;
    private T content;

    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public static <T> ApiResponse<T> success(T content, String message) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .content(content)
                .build();
    }

    public static <T> ApiResponse<T> success(T content) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message(ResponseStatus.SUCCESS.getMessage())
                .content(content)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .content(null)
                .build();
    }

    public static <T> ApiResponse<T> created(T content) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.CREATED.value())
                .message(ResponseStatus.SUCCESS.getMessage())
                .content(content)
                .build();
    }

    public static <T> ApiResponse<T> created(T content, String message) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.CREATED.value())
                .message(message)
                .content(content)
                .build();
    }
}
