package com.teatro.theater.adapters.input;

import com.teatro.theater.adapters.input.dto.CreateTheaterRequest;
import com.teatro.theater.adapters.input.dto.TheaterResponse;
import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.ports.input.CreateTheaterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/theater")
public class TheaterController {

    private final CreateTheaterUseCase createTheaterUseCase;

    public TheaterController(CreateTheaterUseCase createTheaterUseCase) {
        this.createTheaterUseCase = createTheaterUseCase;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheaterResponse> create(@Valid @RequestBody CreateTheaterRequest request) {
        System.out.println("Chegou");
        Theater domain = new Theater(request.name(), request.address(), request.city(), request.capacity());
        Theater saved = createTheaterUseCase.execute(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(TheaterResponse.fromDomain(saved));
    }
}
