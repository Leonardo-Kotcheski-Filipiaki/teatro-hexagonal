package com.teatro.shared.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
