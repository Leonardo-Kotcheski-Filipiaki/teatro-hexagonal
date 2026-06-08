package com.teatro.reservation.adapters.input.dto;

import com.teatro.reservation.domain.model.Seat;
import com.teatro.shared.domain.enums.SeatStatus;

import java.time.LocalDateTime;

public record SeatResponse(
        Long Id,
        String seatCode,
        SeatStatus status,
        LocalDateTime reservedAt
) {
    public static SeatResponse fromDomain(Seat seat) {
        return new SeatResponse(seat.getId(),
                seat.getSeatCode(),
                seat.getStatus(),
                seat.getReservedAt()
        );
    }
}
