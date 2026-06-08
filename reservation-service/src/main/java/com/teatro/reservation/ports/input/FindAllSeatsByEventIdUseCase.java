package com.teatro.reservation.ports.input;

import com.teatro.reservation.domain.model.Seat;

import java.util.List;

public interface FindAllSeatsByEventIdUseCase {
    List<Seat> execute(Long eventId);
}
