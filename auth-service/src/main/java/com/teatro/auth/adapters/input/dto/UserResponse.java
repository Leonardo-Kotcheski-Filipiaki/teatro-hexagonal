package com.teatro.auth.adapters.input.dto;

import com.teatro.auth.domain.model.Roles;
import com.teatro.auth.domain.model.User;

public record UserResponse(

        Long id,
        String name,
        String email,
        Roles role,
        boolean active

) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
    }
}