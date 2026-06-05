package com.teatro.reservation.domain.model;

import com.teatro.shared.domain.enums.SeatStatus;

import java.time.LocalDateTime;

public class Seat {
    private Long id;

    private Long eventId;

    private String seatCode;

    private SeatStatus status;

    private LocalDateTime reservedAt;

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public Seat(Long eventId, String seatCode) {
        this.eventId = eventId;
        this.seatCode = seatCode;
        this.status = SeatStatus.D;
    }

    public Seat(Long id, Long eventId, String seatCode, SeatStatus status, LocalDateTime reservedAt) {
        this.id = id;
        this.eventId = eventId;
        this.seatCode = seatCode;
        this.status = status;
        this.reservedAt = reservedAt;
    }

    public String occupy() {
        String seatOccupied = null;
        if (this.status != SeatStatus.D) {
            seatOccupied = (this.seatCode);
        } else {
            this.status = SeatStatus.R;
            this.reservedAt = LocalDateTime.now();
        }
        return seatOccupied;
    }
}
