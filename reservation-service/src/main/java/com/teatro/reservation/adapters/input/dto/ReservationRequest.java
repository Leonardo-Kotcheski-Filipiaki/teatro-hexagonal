package com.teatro.reservation.adapters.input.dto;

import java.util.List;

public record ReservationRequest(
        Long eventId,
        Long userId,
        List<Long> seatIds
) {
}
