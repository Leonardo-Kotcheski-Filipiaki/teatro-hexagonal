package com.teatro.event.ports.output;

import com.teatro.event.domain.model.Theater;

import java.util.Optional;

public interface TheaterCapacityRepositoryPort {

    Theater save(Theater theater);

    Optional<Integer> getCapacity(Long theaterId);
}
