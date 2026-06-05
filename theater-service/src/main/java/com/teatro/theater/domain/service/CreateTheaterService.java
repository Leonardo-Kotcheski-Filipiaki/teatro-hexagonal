package com.teatro.theater.domain.service;

import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.infrastructure.client.TheaterCapacityClient;
import com.teatro.theater.ports.input.CreateTheaterUseCase;
import com.teatro.theater.ports.output.TheaterRepositoryPort;

public class CreateTheaterService implements CreateTheaterUseCase {

    private final TheaterRepositoryPort theaterRepositoryPort;

    private final TheaterCapacityClient theaterCapacityClient;

    public CreateTheaterService(TheaterRepositoryPort theaterRepositoryPort, TheaterCapacityClient theaterCapacityClient) {
        this.theaterRepositoryPort = theaterRepositoryPort;
        this.theaterCapacityClient = theaterCapacityClient;
    }

    @Override
    public Theater execute(Theater theater) {
        if (theater.getCapacity() <= 0) {
            throw new IllegalArgumentException("A capacidade do teatro deve ser maior que 0.");
        }
        Theater saved = theaterRepositoryPort.save(theater);
        theaterCapacityClient.theaterCapacitySync(saved.getId(), saved.getCapacity());
        return saved;
    }
}
