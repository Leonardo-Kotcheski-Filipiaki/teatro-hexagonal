package com.teatro.auth.ports.input;

import com.teatro.auth.domain.model.User;

import java.util.List;

public interface FindAllUsersUseCase {
    List<User> execute();
}
