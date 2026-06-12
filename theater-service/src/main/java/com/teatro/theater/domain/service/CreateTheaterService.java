package com.teatro.theater.domain.service;

import com.teatro.shared.infrastructure.cache.CacheActionEvent;
import com.teatro.shared.domain.event.DomainEventPublisher;
import com.teatro.theater.domain.event.EventTheaterCreated;
import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.ports.input.CreateTheaterUseCase;
import com.teatro.theater.ports.output.TheaterRepositoryPort;

public class CreateTheaterService implements CreateTheaterUseCase {

    private final TheaterRepositoryPort theaterRepositoryPort;

    private final DomainEventPublisher eventPublisher;

    public CreateTheaterService(TheaterRepositoryPort theaterRepositoryPort,
                                DomainEventPublisher eventPublisher) {
        this.theaterRepositoryPort = theaterRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Theater execute(Theater theater) {
        if (theater.getCapacity() <= 0) {
            throw new IllegalArgumentException("A capacidade do teatro deve ser maior que 0.");
        }
        Theater saved = theaterRepositoryPort.save(theater);

        eventPublisher.publish(new EventTheaterCreated(saved.getId(), saved.getCapacity()));

        eventPublisher.publish(new CacheActionEvent("theater", String.valueOf(saved.getId()), null, CacheActionEvent.CacheAction.EVICT));

        return saved;
    }
}
