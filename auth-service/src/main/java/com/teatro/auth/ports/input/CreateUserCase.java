package com.teatro.auth.ports.input;

import com.teatro.auth.domain.model.User;

public interface CreateUserCase {
    User execute(User user);
}
