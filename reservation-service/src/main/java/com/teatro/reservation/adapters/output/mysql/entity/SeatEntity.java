package com.teatro.reservation.adapters.output.mysql.entity;

import com.teatro.shared.domain.enums.SeatStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "seat_code", nullable = false)
    private String seatCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;


    public SeatEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }


    public SeatEntity create(Long id, Long eventId, String seatCode, SeatStatus status, LocalDateTime reservedAt) {
        SeatEntity s = new SeatEntity();
        s.id = id;
        s.eventId = eventId;
        s.seatCode = seatCode;
        s.status = status;
        s.reservedAt = reservedAt;
        return s;
    }
}
