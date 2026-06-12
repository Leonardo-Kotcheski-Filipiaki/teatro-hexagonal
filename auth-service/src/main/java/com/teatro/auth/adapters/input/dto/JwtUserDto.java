package com.teatro.auth.adapters.input.dto;

import com.teatro.shared.domain.enums.Role;

public record JwtUserDto(
        Long id,
        String email,
        Role role
) {}