package com.teatro.auth.adapters.input.dto;

import com.teatro.auth.domain.model.Roles;

public record JwtUserDto(
        Long id,
        String email,
        Roles role
) {}