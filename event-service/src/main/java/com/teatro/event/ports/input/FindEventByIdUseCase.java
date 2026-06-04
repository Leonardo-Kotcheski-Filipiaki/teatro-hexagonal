package com.teatro.event.ports.input;

import com.teatro.event.domain.model.Event;

public interface FindEventByIdUseCase {

    Event execute(Long id);
}
