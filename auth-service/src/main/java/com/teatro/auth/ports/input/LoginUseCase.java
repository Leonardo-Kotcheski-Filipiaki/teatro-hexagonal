package com.teatro.auth.ports.input;

import com.teatro.auth.adapters.input.dto.LoginResponse;

public interface LoginUseCase {
    LoginResponse execute(String email, String password);
}
