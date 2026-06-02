package com.teatro.auth.adapters.input.dto;

import com.teatro.auth.domain.model.Roles;

public record UpdateUserRequest(

        String name,
        String email,
        Roles role,
        Boolean active

) {}