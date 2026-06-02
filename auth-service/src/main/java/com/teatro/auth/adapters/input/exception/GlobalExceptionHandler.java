package com.teatro.auth.adapters.input.exception;

import com.teatro.auth.adapters.input.dto.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Mapeia todos os campos que falharam na validação
        List<StandardError.FieldErrorValidation> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new StandardError.FieldErrorValidation(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Erro de Validação",
                "Um ou mais campos estão inválidos.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409 Conflict ou 400 Bad Request, você escolhe

        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Regra de Negócio Violada",
                e.getMessage(), // Pega a mensagem que você escreveu no CreateUserService
                request.getRequestURI(),
                null // Sem erros de campos específicos aqui
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardError> handleIllegalState(IllegalStateException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError error = new StandardError(Instant.now(), status.value(), "Acesso Negado", e.getMessage(), request.getRequestURI(), null);
        return ResponseEntity.status(status).body(error);
    }
}
