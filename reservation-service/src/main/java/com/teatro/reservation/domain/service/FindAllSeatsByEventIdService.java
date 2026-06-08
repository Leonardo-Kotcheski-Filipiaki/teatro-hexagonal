package com.teatro.reservation.domain.service;

import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.input.FindAllSeatsByEventIdUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;

import java.util.List;

public class FindAllSeatsByEventIdService implements FindAllSeatsByEventIdUseCase {

    private final ReservationRepositoryPort repository;

    public FindAllSeatsByEventIdService(ReservationRepositoryPort repository) {
        this.repository = repository;
    }


    @Override
    public List<Seat> execute(Long eventId) {
        return repository.findAllSeatsByEventId(eventId);
    }
}
