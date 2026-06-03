package com.teatro.theater.domain.service;

import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.ports.input.FindTheaterByIdUseCase;
import com.teatro.theater.ports.output.TheaterRepositoryPort;


public class FindTheaterByIdService implements FindTheaterByIdUseCase {

    private final TheaterRepositoryPort theaterRepositoryPort;

    public FindTheaterByIdService(TheaterRepositoryPort theaterRepositoryPort) {
        this.theaterRepositoryPort = theaterRepositoryPort;
    }

    @Override
    public Theater execute(Long id) {
        return theaterRepositoryPort.listId(id).orElseThrow(() -> new IllegalArgumentException("Teatro com o ID " + id + " não foi encontrado."));
    }
}
