package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Event;
import com.teatro.event.ports.input.FindAllEventUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;

import java.util.List;

public class FindAllEventService implements FindAllEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    public FindAllEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }


    @Override
    public List<Event> execute() {
        return eventRepositoryPort.list();
    }
}
