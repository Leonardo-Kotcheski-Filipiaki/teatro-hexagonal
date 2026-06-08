package com.teatro.reservation.domain.model;

import com.teatro.shared.domain.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private Long id;

    private Long eventId;

    private Long userId;

    private BookingStatus status;

    private LocalDateTime bookedAt;

    private List<Long> seatIds;

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public Booking() {}

    public Booking(Long eventId, Long userId, List<Long> seatIds) {
        this.eventId = eventId;
        this.userId = userId;
        this.status = BookingStatus.CONFIRMED;
        this.seatIds = seatIds;
        this.bookedAt = null;
    }

    public Booking(Long id, Long eventId, Long userId, BookingStatus status, LocalDateTime bookedAt, List<Long> seatIds) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.status = status;
        this.bookedAt = bookedAt;
        this.seatIds = seatIds;
    }

}
