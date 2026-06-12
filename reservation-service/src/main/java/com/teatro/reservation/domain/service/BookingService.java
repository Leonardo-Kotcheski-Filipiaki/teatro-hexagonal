package com.teatro.reservation.domain.service;

import com.teatro.reservation.domain.model.Booking;
import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.input.BookingUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;
import com.teatro.shared.infrastructure.cache.CacheActionEvent;
import com.teatro.shared.domain.event.DomainEventPublisher;

import java.util.List;
import java.util.Objects;

public class BookingService implements BookingUseCase {

    private final ReservationRepositoryPort repository;

    private final DomainEventPublisher eventPublisher;

    public BookingService(ReservationRepositoryPort repository,
                          DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Booking execute(Long eventId, Long userId, List<Long> seatIds) {
        List<Seat> seatsToReserve = repository.findSeatsByIds(seatIds);

        if (seatsToReserve.size() != seatIds.size()) {
            throw new IllegalArgumentException("Um ou mais assentos selecionados não foram encontrados.");
        }

        boolean allSeatsBelongToEvent = seatsToReserve.stream()
                .allMatch(seat -> seat.getEventId().equals(eventId));
        if(!allSeatsBelongToEvent) {
            throw new IllegalArgumentException("Alguns assentos selecionados não pertencem a este evento.");
        }

        List<String> seatsOccupied = seatsToReserve.stream().map(Seat::occupy).filter(Objects::nonNull).toList();

        if (!seatsOccupied.isEmpty()) {
            if (seatsOccupied.size() > 1) {
                throw new IllegalStateException("Os assentos " + String.join(", ", seatsOccupied) + " não estão disponiveis para reserva.");
            } else {
                throw new IllegalStateException("O assento " + seatsOccupied.getFirst() + " não está disponível para reserva.");
            }

        }
        Booking newBooking = new Booking(eventId, userId, seatIds);

        repository.saveAllSeats(seatsToReserve);
        Booking bookingSalvo = repository.saveBooking(newBooking);

        eventPublisher.publish(new CacheActionEvent("bookings_user_event", String.valueOf(userId), null, CacheActionEvent.CacheAction.EVICT));

        eventPublisher.publish(new CacheActionEvent("bookings_user", String.valueOf(userId), null, CacheActionEvent.CacheAction.EVICT));

        return bookingSalvo;
    }
}
