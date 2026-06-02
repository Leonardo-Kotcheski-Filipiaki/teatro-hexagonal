package com.teatro.auth.dto;

import com.teatro.auth.enums.Roles;

public record JwtUserDto(
        Long id,
        String email,
        Roles role
) {}