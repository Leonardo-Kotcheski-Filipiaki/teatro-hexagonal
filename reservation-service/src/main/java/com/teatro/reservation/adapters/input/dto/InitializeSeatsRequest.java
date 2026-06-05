package com.teatro.reservation.adapters.input.dto;

public record InitializeSeatsRequest(
        Long eventId, int totalSeats
) {
}
