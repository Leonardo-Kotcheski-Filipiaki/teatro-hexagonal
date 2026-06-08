package com.teatro.shared.infrastructure.event;

import com.teatro.shared.domain.event.DomainEvent;
import com.teatro.shared.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish (DomainEvent event) {
        this.springPublisher.publishEvent(event);
    }
}
