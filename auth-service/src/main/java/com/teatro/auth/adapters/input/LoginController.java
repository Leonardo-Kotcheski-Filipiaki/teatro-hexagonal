package com.teatro.auth.adapters.input;


import com.teatro.auth.adapters.input.dto.LoginRequest;
import com.teatro.auth.adapters.input.dto.LoginResponse;
import com.teatro.auth.ports.input.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginUseCase loginUseCase;

    public LoginController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUseCase.execute(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
