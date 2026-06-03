package com.teatro.theater.ports.input;

import com.teatro.theater.domain.model.Theater;

import java.util.List;

public interface FindAllTheatersUseCase {

    List<Theater> execute();

}
