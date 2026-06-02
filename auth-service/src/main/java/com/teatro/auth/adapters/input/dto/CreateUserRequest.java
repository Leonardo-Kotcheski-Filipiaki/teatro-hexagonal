package com.teatro.auth.adapters.input.dto;

import com.teatro.auth.domain.model.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 255, message = "O nome deve conter entre 3 e 255 caracteres.")
        String name,

        @Email(message = "O e-mail deve ser válido.")
        @NotBlank(message = "O e-mail é obrigatório.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve conter no minimo 6 caracteres.")
        String password,

        @NotNull(message = "A role do usuário é obrigatória.")
        Roles role

) {}