package com.teatro.theater.infrastructure;

import com.teatro.theater.domain.service.CreateTheaterService;
import com.teatro.theater.ports.input.CreateTheaterUseCase;
import com.teatro.theater.ports.output.TheaterRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateTheaterUseCase createTheaterUseCase(TheaterRepositoryPort theaterRepositoryPort) {
        return new CreateTheaterService(theaterRepositoryPort);
    }
}
