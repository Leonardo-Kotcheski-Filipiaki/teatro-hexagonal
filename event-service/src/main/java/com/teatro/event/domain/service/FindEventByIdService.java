package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Event;
import com.teatro.event.ports.input.FindEventByIdUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;

public class FindEventByIdService implements FindEventByIdUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    public FindEventByIdService (EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }


    @Override
    public Event execute(Long id) {
        return eventRepositoryPort.listId(id).orElseThrow(() -> new IllegalArgumentException("Teatro com o ID " + id + " não foi encontrado."));
    }
}
