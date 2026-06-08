package com.teatro.theater.domain.event;

import com.teatro.shared.domain.event.DomainEvent;

public record EventTheaterCreated(
        Long id,
        Integer capacity
) implements DomainEvent {

}
