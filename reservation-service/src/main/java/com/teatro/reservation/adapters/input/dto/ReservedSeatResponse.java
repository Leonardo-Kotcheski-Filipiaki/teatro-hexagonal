package com.teatro.reservation.adapters.input.dto;

public record ReservedSeatResponse(
        Long id,
        String seatCode,
        Long eventId
) {
}
