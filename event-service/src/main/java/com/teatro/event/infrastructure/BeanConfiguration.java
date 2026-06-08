package com.teatro.event.infrastructure;

import com.teatro.event.domain.service.*;
import com.teatro.event.ports.input.*;
import com.teatro.event.ports.output.EventRepositoryPort;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;
import com.teatro.shared.domain.event.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateEventUseCase createEventUseCase(EventRepositoryPort eventRepositoryPort,
                                                 TheaterCapacityRepositoryPort theaterCapacityRepositoryPort,
                                                 DomainEventPublisher eventPublisher) {
        return new CreateEventService(eventRepositoryPort, theaterCapacityRepositoryPort, eventPublisher);
    }

    @Bean
    public FindAllEventUseCase findAllEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new FindAllEventService(eventRepositoryPort);
    }

    @Bean
    public FindEventByIdUseCase findEventByIdUseCase(EventRepositoryPort eventRepositoryPort) {
        return new FindEventByIdService(eventRepositoryPort);
    }

    @Bean
    public TheaterCapacitySyncUseCase theaterCapacitySyncUseCase(TheaterCapacityRepositoryPort repositoryPort) {
        return new TheaterCapacitySyncService(repositoryPort);
    }
}
