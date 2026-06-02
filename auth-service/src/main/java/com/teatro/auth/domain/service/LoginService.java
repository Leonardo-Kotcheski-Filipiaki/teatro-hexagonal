package com.teatro.auth.domain.service;

import com.teatro.auth.domain.model.User;
import com.teatro.auth.ports.input.LoginUseCase;
import com.teatro.auth.ports.output.PasswordEncryptionPort;
import com.teatro.auth.ports.output.TokenServicePort;
import com.teatro.auth.ports.output.UserRepositoryPort;

public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncryptionPort passwordEncryptionPort;
    private final TokenServicePort tokenServicePort;

    public LoginService(UserRepositoryPort userRepositoryPort,
                        PasswordEncryptionPort passwordEncryptionPort,
                        TokenServicePort tokenServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncryptionPort = passwordEncryptionPort;
        this.tokenServicePort = tokenServicePort;
    }

    @Override
    public String execute(String email, String password) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos."));
        if (!user.isActive()) {
            throw new IllegalStateException("Esta conta está desativada.");
        }

        if (!passwordEncryptionPort.matches(password, user.getPasswordhash())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos.");
        }

        return tokenServicePort.generateToken(user);
    }
}
