package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Theater;
import com.teatro.event.ports.input.TheaterCapacitySyncUseCase;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;

public class TheaterCapacitySyncService implements TheaterCapacitySyncUseCase {

    private final TheaterCapacityRepositoryPort repositoryPort;

    public TheaterCapacitySyncService(TheaterCapacityRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }


    @Override
    public Theater execute(Theater theater) {
        return repositoryPort.save(theater);
    }
}
