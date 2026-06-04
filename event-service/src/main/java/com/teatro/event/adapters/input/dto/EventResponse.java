package com.teatro.event.adapters.input.dto;


import com.teatro.event.domain.model.Event;
import com.teatro.shared.domain.enums.EventStatus;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        Long theaterId,
        String name,
        LocalDateTime eventDate,
        Integer totalSeats,
        Integer availableSeats,
        EventStatus status
) {

    public static EventResponse fromDomain(Event event) {
        return new EventResponse(event.getId(),
                                 event.getTheaterId(),
                                 event.getName(),
                                 event.getEventDate(),
                                 event.getTotalSeats(),
                                 event.getAvailableSeats(),
                                 event.getStatus());
    }
}
