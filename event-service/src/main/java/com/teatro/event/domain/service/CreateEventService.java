package com.teatro.event.domain.service;

import com.teatro.event.domain.event.EventCreatedEvent;
import com.teatro.event.domain.model.Event;
import com.teatro.event.domain.model.Theater;
import com.teatro.event.ports.input.CreateEventUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;
import com.teatro.shared.infrastructure.cache.CacheActionEvent;
import com.teatro.shared.domain.event.DomainEventPublisher;

public class CreateEventService implements CreateEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    private final TheaterCapacityRepositoryPort theaterCapacityRepositoryPort;

    private final DomainEventPublisher eventPublisher;

    public CreateEventService(EventRepositoryPort eventRepositoryPort,
                              TheaterCapacityRepositoryPort theaterCapacityRepositoryPort,
                              DomainEventPublisher eventPublisher) {
        this.eventRepositoryPort = eventRepositoryPort;
        this.theaterCapacityRepositoryPort = theaterCapacityRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Event execute(Event event) {
        Theater theater = theaterCapacityRepositoryPort.getCapacity(event.getTheaterId());
        if (event.getTotalSeats() > theater.getCapacity()) {
            throw new IllegalArgumentException("A necessidade de assentos do evento supera a capacidade máxima do teatro!\nCapacidade máxima: " + theater.getCapacity());
        }
        Event retorno = eventRepositoryPort.save(event);

        eventPublisher.publish(new EventCreatedEvent(retorno.getId(), retorno.getTotalSeats()));

        eventPublisher.publish(new CacheActionEvent("events", "list", null, CacheActionEvent.CacheAction.EVICT));

        return retorno;
    }
}
