package com.teatro.auth.ports.input;

public interface LoginUseCase {
    String execute(String email, String password);
}
