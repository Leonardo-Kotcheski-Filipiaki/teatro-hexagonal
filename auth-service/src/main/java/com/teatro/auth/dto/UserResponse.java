package com.teatro.auth.dto;

import com.teatro.auth.enums.Roles;

public record UserResponse(

        Long id,
        String name,
        String email,
        Roles role,
        boolean active

) {}