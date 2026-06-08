package com.teatro.reservation.domain.service;

import com.teatro.reservation.domain.model.Booking;
import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.input.FindUserReservedSeatsUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class FindUserReservedService implements FindUserReservedSeatsUseCase {

    private final ReservationRepositoryPort repository;

    public FindUserReservedService(ReservationRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Seat> execute(Long eventId, Long userId) {
        List<Booking> bookings = repository.findUserSeats(eventId, userId);
        List<Long> seatIds = bookings.stream()
                .flatMap(booking -> booking.getSeatIds().stream())
                .collect(Collectors.toList());

        if (seatIds.isEmpty()) {
            return List.of();
        }

        return repository.findSeatsByIds(seatIds);
    }

}
