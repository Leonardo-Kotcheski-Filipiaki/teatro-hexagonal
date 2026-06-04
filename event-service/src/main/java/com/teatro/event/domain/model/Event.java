package com.teatro.event.domain.model;

import com.teatro.shared.domain.enums.EventStatus;

import java.time.LocalDateTime;

public class Event {

    private Long id;

    private Long theaterId;

    private String name;

    private LocalDateTime eventDate;

    private Integer totalSeats;

    private Integer availableSeats;

    private EventStatus status;

    public Long getId() {
        return id;
    }

    public Long getTheaterId() {
        return theaterId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public EventStatus getStatus() {
        return status;
    }

    public Event(Long theaterId, String name, LocalDateTime eventDate, Integer totalSeats) {
        this.theaterId = theaterId;
        this.name = name;
        this.eventDate = eventDate;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.status = EventStatus.ACTIVE;
    }

    public Event(Long theaterId, String name, LocalDateTime eventDate, Integer totalSeats, Integer availableSeats, EventStatus status) {
        this.theaterId = theaterId;
        this.name = name;
        this.eventDate = eventDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    public Event(Long id, Long theaterId, String name, LocalDateTime eventDate, Integer totalSeats, Integer availableSeats, EventStatus status) {
        this.id = id;
        this.theaterId = theaterId;
        this.name = name;
        this.eventDate = eventDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.status = status;
    }
}
