package com.teatro.reservation.ports.input;

public interface InitializeSeatsUseCase {
    void execute(Long eventId, int totalSeats);
}
