package com.teatro.event.ports.output;

import com.teatro.event.domain.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {

    Event save(Event event);

    List<Event> list();

    Optional<Event> listId(Long Id);

}
