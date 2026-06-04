package com.teatro.event.infrastructure;

import com.teatro.event.domain.service.CreateEventService;
import com.teatro.event.domain.service.FindAllEventService;
import com.teatro.event.domain.service.FindEventByIdService;
import com.teatro.event.ports.input.CreateEventUseCase;
import com.teatro.event.ports.input.FindAllEventUseCase;
import com.teatro.event.ports.input.FindEventByIdUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateEventUseCase createEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new CreateEventService(eventRepositoryPort);
    }

    @Bean
    public FindAllEventUseCase findAllEventUseCase(EventRepositoryPort eventRepositoryPort) {
        return new FindAllEventService(eventRepositoryPort);
    }

    @Bean
    public FindEventByIdUseCase findEventByIdUseCase(EventRepositoryPort eventRepositoryPort) {
        return new FindEventByIdService(eventRepositoryPort);
    }
}
