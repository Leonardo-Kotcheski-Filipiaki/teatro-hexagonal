package com.teatro.reservation.adapters.output.mysql.entity;

import com.teatro.shared.domain.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id",nullable = false)
    private Long eventId;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @ManyToMany
    @JoinTable(
            name = "booking_seat", // Nome da tabela associativa do seu script
            joinColumns = @JoinColumn(name = "booking_id"), // FK para esta tabela (Booking)
            inverseJoinColumns = @JoinColumn(name = "seat_id") // FK para a tabela de Seat
    )
    private List<SeatEntity> seats;

    public BookingEntity() {}

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public List<SeatEntity> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatEntity> seats) {
        this.seats = seats;
    }

    public static BookingEntity create(Long id, Long eventId, Long userId, BookingStatus status, LocalDateTime bookedAt) {
        BookingEntity b = new BookingEntity();
        b.id = id;
        b.eventId = eventId;
        b.userId = userId;
        b.status = status;
        b.bookedAt = bookedAt;
        return b;
    }
}
