package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Theater;
import com.teatro.event.ports.input.FindByIdTheaterCapacityUseCase;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;

public class FindTheaterByIdService implements FindByIdTheaterCapacityUseCase {

    private final TheaterCapacityRepositoryPort repositoryPort;

    public FindTheaterByIdService (TheaterCapacityRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }


    @Override
    public Theater execute(Long id) {
        return repositoryPort.list(id).orElseThrow(() -> new IllegalArgumentException("Teatro com o ID " + id + " não foi encontrado."));
    }
}
