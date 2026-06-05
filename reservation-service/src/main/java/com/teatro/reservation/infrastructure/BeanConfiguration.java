package com.teatro.reservation.infrastructure;

import com.teatro.reservation.domain.service.ReservationService;
import com.teatro.reservation.ports.input.BookingUseCase;
import com.teatro.reservation.ports.input.InitializeSeatsUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public BookingUseCase BookingUseCase(ReservationRepositoryPort repositoryPort) {
        return new ReservationService(repositoryPort);
    }

    @Bean
    public InitializeSeatsUseCase initializeSeatsUseCase(ReservationRepositoryPort repositoryPort) {
        return new ReservationService(repositoryPort);
    }
}
