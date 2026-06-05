package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Event;
import com.teatro.event.infrastructure.client.ReservationClient;
import com.teatro.event.ports.input.CreateEventUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;

public class CreateEventService implements CreateEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    private final TheaterCapacityRepositoryPort theaterCapacityRepositoryPort;

    private final ReservationClient reservationClient;

    public CreateEventService(EventRepositoryPort eventRepositoryPort,
                              ReservationClient reservationClient,
                              TheaterCapacityRepositoryPort theaterCapacityRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
        this.reservationClient = reservationClient;
        this.theaterCapacityRepositoryPort = theaterCapacityRepositoryPort;
    }

    @Override
    public Event execute(Event event) {
        Integer capactity = theaterCapacityRepositoryPort.getCapacity(event.getTheaterId()).orElseThrow(() -> new IllegalArgumentException("O teatro com id " + event.getTheaterId() + " não foi encontrado!"));
        if (event.getTotalSeats() > capactity) {
            throw new IllegalArgumentException("A necessidade de assentos do evento supera a capacidade máxima do teatro!\nCapacidade máxima: " + capactity);
        }
        Event retorno = eventRepositoryPort.save(event);
        reservationClient.initializeSeats(retorno.getId(), retorno.getTotalSeats());
        return retorno;
    }
}
