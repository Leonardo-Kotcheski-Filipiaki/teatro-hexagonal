package com.teatro.auth.adapters.input;

import com.teatro.auth.adapters.input.dto.UserResponse;
import com.teatro.auth.ports.input.FindAllUsersUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final FindAllUsersUseCase findAllUsersUseCase;

    public UsersController(FindAllUsersUseCase findAllUsersUseCase) {
        this.findAllUsersUseCase = findAllUsersUseCase;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(findAllUsersUseCase.execute().stream().map(UserResponse::fromDomain).toList());
    }
}
