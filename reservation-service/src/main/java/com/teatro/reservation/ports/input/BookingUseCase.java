package com.teatro.reservation.ports.input;

import com.teatro.reservation.domain.model.Booking;

import java.util.List;

public interface BookingUseCase {
    Booking execute(Long eventId, Long userId, List<Long> seatIds);
}
