package com.teatro.theater.adapters.input;

import com.teatro.theater.adapters.input.dto.CreateTheaterRequest;
import com.teatro.theater.adapters.input.dto.TheaterResponse;
import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.ports.input.CreateTheaterUseCase;
import com.teatro.theater.ports.input.FindAllTheatersUseCase;
import com.teatro.theater.ports.input.FindTheaterByIdUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theater")
public class TheaterController {

    private final CreateTheaterUseCase createTheaterUseCase;

    private final FindAllTheatersUseCase findAllTheatersUseCase;

    private final FindTheaterByIdUseCase findTheaterByIdUseCase;

    public TheaterController(CreateTheaterUseCase createTheaterUseCase,
                             FindAllTheatersUseCase findAllTheatersUseCase,
                             FindTheaterByIdUseCase findTheaterByIdUseCase) {
        this.createTheaterUseCase = createTheaterUseCase;
        this.findAllTheatersUseCase = findAllTheatersUseCase;
        this.findTheaterByIdUseCase = findTheaterByIdUseCase;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<TheaterResponse> create(@Valid @RequestBody CreateTheaterRequest request) {
        Theater domain = new Theater(request.name(), request.address(), request.city(), request.capacity());
        Theater saved = createTheaterUseCase.execute(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(TheaterResponse.fromDomain(saved));
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<TheaterResponse>> listAll() {
        List<TheaterResponse> lista = findAllTheatersUseCase.execute().stream().map(TheaterResponse::fromDomain).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TheaterResponse> listId(@PathVariable Long id) {
        Theater retorno = findTheaterByIdUseCase.execute(id);
        return ResponseEntity.ok(TheaterResponse.fromDomain(retorno));
    }
}
