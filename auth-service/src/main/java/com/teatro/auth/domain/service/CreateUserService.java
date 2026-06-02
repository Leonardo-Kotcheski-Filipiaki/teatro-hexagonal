package com.teatro.auth.domain.service;

import com.teatro.auth.domain.model.User;
import com.teatro.auth.ports.input.CreateUserCase;
import com.teatro.auth.ports.output.UserRepositoryPort;

public class CreateUserService implements CreateUserCase {

    private final UserRepositoryPort userRepositoryPort;

    public CreateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(User user) {
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema");
        }
        return userRepositoryPort.save(user);
    }
}
