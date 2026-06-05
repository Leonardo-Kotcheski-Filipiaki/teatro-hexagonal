package com.teatro.event.infrastructure;

import com.teatro.event.domain.service.*;
import com.teatro.event.infrastructure.client.ReservationClient;
import com.teatro.event.ports.input.*;
import com.teatro.event.ports.output.EventRepositoryPort;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateEventUseCase createEventUseCase(EventRepositoryPort eventRepositoryPort,
                                                 ReservationClient reservationClient,
                                                 TheaterCapacityRepositoryPort theaterCapacityRepositoryPort) {
        return new CreateEventService(eventRepositoryPort, reservationClient, theaterCapacityRepositoryPort);
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
