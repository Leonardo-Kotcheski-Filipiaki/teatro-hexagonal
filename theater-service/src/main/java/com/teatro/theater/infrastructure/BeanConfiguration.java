package com.teatro.theater.infrastructure;

import com.teatro.theater.domain.service.CreateTheaterService;
import com.teatro.theater.domain.service.FindAllTheatersService;
import com.teatro.theater.domain.service.FindTheaterByIdService;
import com.teatro.theater.infrastructure.client.TheaterCapacityClient;
import com.teatro.theater.ports.input.CreateTheaterUseCase;
import com.teatro.theater.ports.input.FindAllTheatersUseCase;
import com.teatro.theater.ports.input.FindTheaterByIdUseCase;
import com.teatro.theater.ports.output.TheaterRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateTheaterUseCase createTheaterUseCase(TheaterRepositoryPort theaterRepositoryPort, TheaterCapacityClient theaterCapacityClient) {
        return new CreateTheaterService(theaterRepositoryPort, theaterCapacityClient);
    }

    @Bean
    public FindAllTheatersUseCase findAllTheatersUseCase(TheaterRepositoryPort theaterRepositoryPort) {
        return new FindAllTheatersService(theaterRepositoryPort);
    }

    @Bean
    public FindTheaterByIdUseCase findTheaterByIdUseCase(TheaterRepositoryPort theaterRepositoryPort) {
        return new FindTheaterByIdService(theaterRepositoryPort);
    }
}
