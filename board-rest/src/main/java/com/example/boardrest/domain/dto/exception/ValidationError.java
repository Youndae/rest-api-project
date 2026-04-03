package com.example.boardrest.domain.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String constraint;
    private String validationMessage;
}
