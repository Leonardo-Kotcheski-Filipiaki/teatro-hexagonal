package com.teatro.event.adapters.input;

import com.teatro.event.adapters.input.dto.CreateEventRequest;
import com.teatro.event.adapters.input.dto.EventResponse;
import com.teatro.event.adapters.input.dto.TheaterSyncRequest;
import com.teatro.event.domain.model.Event;
import com.teatro.event.domain.model.Theater;
import com.teatro.event.ports.input.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final CreateEventUseCase createEventUseCase;

    private final FindAllEventUseCase findAllEventUseCase;

    private final FindEventByIdUseCase findEventByIdUseCase;

    private final TheaterCapacitySyncUseCase theaterCapacitySyncUseCase;


    public EventController(CreateEventUseCase createEventUseCase,
                           FindAllEventUseCase findAllEventUseCase,
                           FindEventByIdUseCase findEventByIdUseCase,
                           TheaterCapacitySyncUseCase theaterCapacitySyncUseCase) {
        this.createEventUseCase = createEventUseCase;
        this.findAllEventUseCase = findAllEventUseCase;
        this.findEventByIdUseCase = findEventByIdUseCase;
        this.theaterCapacitySyncUseCase = theaterCapacitySyncUseCase;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {

        Event domain = new Event(request.theaterId(), request.name(), request.eventDate(), request.totalSeats());
        Event saved = createEventUseCase.execute(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.fromDomain(saved));
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<EventResponse>> listAll() {
        List<EventResponse> lista = findAllEventUseCase.execute().stream().map(EventResponse::fromDomain).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<EventResponse> listId(@PathVariable Long id) {
        Event retorno = findEventByIdUseCase.execute(id);
        return ResponseEntity.ok(EventResponse.fromDomain(retorno));
    }

    @PostMapping("/internal/theater/sync/capacity")
    public ResponseEntity<Void> syncTheater(@RequestBody TheaterSyncRequest request) {
        theaterCapacitySyncUseCase.execute(new Theater(request.theaterId(), request.capacity()));
        return ResponseEntity.ok().build();
    }
}
