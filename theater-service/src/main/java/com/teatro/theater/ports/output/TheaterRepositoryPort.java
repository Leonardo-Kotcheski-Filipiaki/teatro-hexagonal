package com.teatro.theater.ports.output;

import com.teatro.theater.domain.model.Theater;

public interface TheaterRepositoryPort {

    Theater save(Theater theater);
}
