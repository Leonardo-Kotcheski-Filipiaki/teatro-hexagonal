package com.teatro.event.domain.service;

import com.teatro.event.domain.model.Event;
import com.teatro.event.infrastructure.client.ReservationClient;
import com.teatro.event.ports.input.CreateEventUseCase;
import com.teatro.event.ports.output.EventRepositoryPort;

public class CreateEventService implements CreateEventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    private final ReservationClient reservationClient;

    public CreateEventService(EventRepositoryPort eventRepositoryPort, ReservationClient reservationClient) {
        this.eventRepositoryPort = eventRepositoryPort;
        this.reservationClient = reservationClient;
    }

    @Override
    public Event execute(Event event) {
        Event retorno = eventRepositoryPort.save(event);
        reservationClient.initializeSeats(retorno.getId(), retorno.getTotalSeats());
        return retorno;
    }
}
