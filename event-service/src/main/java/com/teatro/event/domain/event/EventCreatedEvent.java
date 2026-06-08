package com.teatro.event.domain.event;

import com.teatro.shared.domain.event.DomainEvent;

public record EventCreatedEvent(
        Long eventId,
        int totalSeats
) implements DomainEvent {
}
