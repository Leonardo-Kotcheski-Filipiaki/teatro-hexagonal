package com.teatro.event.adapters.input.dto;

public record InitializeSeatsRequest(
        Long eventId, int totalSeats
) {
}
