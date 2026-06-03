package com.teatro.theater.ports.output;

import com.teatro.theater.domain.model.Theater;

import java.util.List;
import java.util.Optional;

public interface TheaterRepositoryPort {

    Theater save(Theater theater);

    List<Theater> list();

    Optional<Theater> listId(Long id);
}
