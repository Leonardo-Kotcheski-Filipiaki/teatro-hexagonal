package com.teatro.auth.domain.service;

import com.teatro.auth.domain.model.User;
import com.teatro.auth.ports.input.FindAllUsersUseCase;
import com.teatro.auth.ports.output.UserRepositoryPort;

import java.util.List;

public class FindAllUsersService implements FindAllUsersUseCase {

    private final UserRepositoryPort repositoryPort;

    public FindAllUsersService(UserRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }


    @Override
    public List<User> execute() {
        return repositoryPort.findAllUsers();
    }
}
