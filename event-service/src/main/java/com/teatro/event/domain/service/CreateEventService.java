package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Event;
import com.teatro.event.ports.input.CreateEventUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;

public class CreateEventService implements CreateEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    public CreateEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    public Event execute(Event event) {
        return eventRepositoryPort.save(event);
    }
}
