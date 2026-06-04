package com.teatro.event.ports.input;

import com.teatro.event.domain.model.Event;

public interface CreateEventUseCase {

    Event execute(Event event);
}
