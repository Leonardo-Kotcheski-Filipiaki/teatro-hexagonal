package com.teatro.auth.dto;

import com.teatro.auth.enums.Roles;

public record LoginResponse(

        Long id,
        String name,
        String email,
        Roles role,
        String token

) {}