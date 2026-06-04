package com.teatro.event.ports.input;

import com.teatro.event.domain.model.Event;

import java.util.List;

public interface FindAllEventUseCase {

    List<Event> execute();
}
