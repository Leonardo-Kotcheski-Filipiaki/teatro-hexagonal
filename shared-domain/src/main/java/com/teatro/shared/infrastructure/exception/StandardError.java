package com.teatro.shared.infrastructure.exception;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path,
        java.util.List<String> errors
) {}