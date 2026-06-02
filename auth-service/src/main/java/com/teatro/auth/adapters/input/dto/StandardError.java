package com.teatro.auth.adapters.input.dto;

import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;

public record StandardError (

    Instant timestamp,
    Integer status,
    String error,
    String message,
    String Path,
    List<FieldErrorValidation> errors
) {
    public record FieldErrorValidation(String Field, String message){}
}
