package com.teatro.theater.adapters.input.exception;

import com.teatro.shared.infrastructure.exception.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Captura erros de regras de negócio do Domínio (ex: capacidade <= 0)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Requisição Inválida",
                e.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(error);
    }

    // 2. Captura erros do @Valid (ex: @NotBlank, @Min no seu CreateTheaterRequest)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422

        // Captura todos os erros de campos específicos do DTO
        List<String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Erro de Validação nos campos",
                "Existem campos inválidos no envio.",
                request.getRequestURI(),
                fieldErrors // Injeta a lista de erros detalhados aqui
        );

        return ResponseEntity.status(status).body(error);
    }

    // 3. Captura erros do @PreAuthorize("hasRole('ADMIN')") quando o token não for Admin
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN; // 403

        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Acesso Proibido",
                "Você não tem permissão para acessar este recurso. Apenas Administradores.",
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(error);
    }

}
