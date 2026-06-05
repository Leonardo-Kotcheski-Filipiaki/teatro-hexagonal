package com.teatro.event.ports.input;

import com.teatro.event.domain.model.Theater;

public interface TheaterCapacitySyncUseCase {

    Theater execute(Theater theater);
}
