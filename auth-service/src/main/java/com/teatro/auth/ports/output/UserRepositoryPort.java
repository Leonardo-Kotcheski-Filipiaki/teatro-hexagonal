package com.teatro.auth.ports.output;

import com.teatro.auth.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
