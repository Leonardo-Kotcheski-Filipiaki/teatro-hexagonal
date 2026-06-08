package com.teatro.reservation.infrastructure;

import com.teatro.reservation.domain.service.*;
import com.teatro.reservation.ports.input.*;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;
import com.teatro.shared.domain.event.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public BookingUseCase BookingUseCase(ReservationRepositoryPort repositoryPort,
                                         DomainEventPublisher eventPublisher) {
        return new BookingService(repositoryPort, eventPublisher);
    }

    @Bean
    public InitializeSeatsUseCase initializeSeatsUseCase(ReservationRepositoryPort repositoryPort) {
        return new InitializeSeatsService(repositoryPort);
    }

    @Bean
    public FindUserReservedSeatsUseCase findUserReservedSeatsUseCase(ReservationRepositoryPort repositoryPort) {
        return new FindUserReservedService(repositoryPort);
    }

    @Bean
    public FindAllUserReservedSeatsUseCase findAllUserReservedSeatsUseCase(ReservationRepositoryPort repositoryPort) {
        return new FindAllUsersReservedSeatsService(repositoryPort);
    }

    @Bean
    public FindAllSeatsByEventIdUseCase findAllSeatsByEventIdUseCase(ReservationRepositoryPort repositoryPort) {
        return new FindAllSeatsByEventIdService(repositoryPort);
    }
}
