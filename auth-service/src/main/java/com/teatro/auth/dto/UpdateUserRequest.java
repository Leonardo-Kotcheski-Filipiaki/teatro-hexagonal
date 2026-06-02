package com.teatro.auth.dto;

import com.teatro.auth.enums.Roles;

public record UpdateUserRequest(

        String name,
        String email,
        Roles role,
        Boolean active

) {}