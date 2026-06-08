package com.teatro.event.ports.output;

import com.teatro.event.domain.model.Theater;

public interface TheaterCapacityRepositoryPort {

    Theater save(Theater theater);

    Theater getCapacity(Long theaterId);
}
