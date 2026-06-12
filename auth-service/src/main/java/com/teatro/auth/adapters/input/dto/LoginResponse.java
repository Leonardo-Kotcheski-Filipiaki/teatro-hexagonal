package com.teatro.auth.adapters.input.dto;

import com.teatro.shared.domain.enums.Role;

public record LoginResponse(
        String token,
        Long id,
        String usuario,
        Role role
) {}