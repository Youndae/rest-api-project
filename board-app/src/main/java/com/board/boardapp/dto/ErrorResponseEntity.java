package com.board.boardapp.dto;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseEntity {

    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(
                        ErrorResponseEntity.builder()
                                .status(e.getHttpStatus().intValue())
                                .code(e.name())
                                .message(e.getMessage())
                                .build()
                );
    }
}
