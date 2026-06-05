package com.teatro.auth.adapters.input;

import com.teatro.auth.adapters.input.dto.CreateUserRequest;
import com.teatro.auth.adapters.input.dto.UserResponse;
import com.teatro.auth.domain.model.Roles;
import com.teatro.auth.domain.model.User;
import com.teatro.auth.ports.input.CreateUserCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final CreateUserCase CreateUserCase;

    private final PasswordEncoder passwordEncoder;

    public RegisterController(CreateUserCase createUserCase, PasswordEncoder passwordEncoder) {
        this.CreateUserCase = createUserCase;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register/internal/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        String passwordHash = passwordEncoder.encode(request.password());
        User newUser = new User(request.name(), request.email(), passwordHash, request.role());

        User savedUser = CreateUserCase.execute(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(savedUser));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<UserResponse> customer(@Valid @RequestBody CreateUserRequest request) {
        if (request.role().equals(Roles.ADMIN)) throw new IllegalArgumentException("Esta rota deve registrar apenas clientes!");
        String passwordHash = passwordEncoder.encode(request.password());
        User newUser = new User(request.name(), request.email(), passwordHash, request.role());

        User savedUser = CreateUserCase.execute(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(savedUser));
    }
}
