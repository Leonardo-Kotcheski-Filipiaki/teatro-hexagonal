package com.teatro.reservation.domain.service;

import com.teatro.reservation.domain.model.Booking;
import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.input.FindAllUserReservedSeatsUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class FindAllUsersReservedSeatsService implements FindAllUserReservedSeatsUseCase {

    private final ReservationRepositoryPort repository;

    public FindAllUsersReservedSeatsService(ReservationRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Seat> execute(Long userId) {
        List<Booking> bookings = repository.findBookingsByUserId(userId);
        List<Long> seatIds = bookings.stream()
                .flatMap(booking -> booking.getSeatIds().stream())
                .collect(Collectors.toList());

        if (seatIds.isEmpty()) {
            return List.of();
        }

        return repository.findSeatsByIds(seatIds);
    }
}
