package com.teatro.reservation.ports.output;

import com.teatro.reservation.domain.model.Booking;
import com.teatro.reservation.domain.model.Seat;

import java.util.List;

public interface ReservationRepositoryPort {

    List<Seat> findSeatsByIds(List<Long> seatIds);

    Booking saveBooking(Booking booking);

    void saveAllSeats(List<Seat> seats);

    List<Booking> findUserSeats(Long eventId, Long userId);

    List<Booking> findBookingsByUserId(Long userId);
}
