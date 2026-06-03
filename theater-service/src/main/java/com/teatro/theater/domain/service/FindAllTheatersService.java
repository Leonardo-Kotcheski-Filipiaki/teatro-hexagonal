package com.teatro.theater.domain.service;

import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.ports.input.FindAllTheatersUseCase;
import com.teatro.theater.ports.output.TheaterRepositoryPort;

import java.util.List;

public class FindAllTheatersService implements FindAllTheatersUseCase {

    private final TheaterRepositoryPort theaterRepositoryPort;

    public FindAllTheatersService(TheaterRepositoryPort theaterRepositoryPort) {
        this.theaterRepositoryPort = theaterRepositoryPort;
    }

    @Override
    public List<Theater> execute() {
        return theaterRepositoryPort.list();
    }
}
