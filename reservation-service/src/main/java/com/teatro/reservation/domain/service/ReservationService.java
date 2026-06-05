package com.teatro.reservation.domain.service;

import com.teatro.reservation.domain.model.Booking;
import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.input.BookingUseCase;
import com.teatro.reservation.ports.input.FindAllUserReservedSeatsUseCase;
import com.teatro.reservation.ports.input.FindUserReservedSeatsUseCase;
import com.teatro.reservation.ports.input.InitializeSeatsUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReservationService implements BookingUseCase, InitializeSeatsUseCase, FindUserReservedSeatsUseCase, FindAllUserReservedSeatsUseCase {

    private final ReservationRepositoryPort repository;

    public ReservationService(ReservationRepositoryPort repository) {
        this.repository = repository;
    }


    @Override
    public Booking execute(Long eventId, Long userId, List<Long> seatIds) {
        //1º regra: Assentos disponíveis!
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
        return repository.saveBooking(newBooking);
    }

    @Override
    public void execute(Long eventId, int totalSeats) {
        // 1. Define quantos assentos terá cada fileira (sua regra de negócio)
        int seatsPerRow = totalSeats / 10;

        // Se o evento for menor que 500 lugares (ou menor que 50 no seu caso),
        // garantimos o mínimo de 5 assentos por caractere
        if (totalSeats < 50 || seatsPerRow < 5) {
            seatsPerRow = 5;
        }

        List<Seat> listaGerada = new ArrayList<>();
        char rowLetter = 'A';  // Começa na fileira A
        int seatNumber = 1;    // Começa no assento 1

        // 2. O loop roda exatamente a quantidade total de assentos solicitados
        for (int i = 1; i <= totalSeats; i++) {

            // Formata o código do assento (Ex: "A-1", "A-2"...)
            String seatCode = rowLetter + "-" + seatNumber;

            Seat novoAssento = new Seat(eventId, seatCode);
            listaGerada.add(novoAssento);

            // Avança para o próximo número de assento
            seatNumber++;

            // Se estourou o limite de assentos daquela fileira, pula para a próxima letra
            if (seatNumber > seatsPerRow) {
                seatNumber = 1;  // Reseta o número para 1
                rowLetter++;     // Avança a letra: 'A' vira 'B', 'B' vira 'C'...
            }
        }

        // 3. Manda salvar tudo usando a porta de saída
        repository.saveAllSeats(listaGerada);
    }

    @Override
    public List<Seat> execute(Long eventId, Long userId) {
        List<Booking> bookings = repository.findUserSeats(eventId, userId);
        List<Long> seatIds = bookings.stream()
                .flatMap(booking -> booking.getSeatIds().stream())
                .collect(Collectors.toList());

        if (seatIds.isEmpty()) {
            return List.of();
        }

        return repository.findSeatsByIds(seatIds);
    }

    @Override
    public List<Seat> execute(Long userId) {
        List<Booking> bookings = repository.findBookingsByUserId(userId);
        List<Long> seatIds = bookings.stream()
                .flatMap(booking -> booking.getSeatIds().stream())
                .collect(Collectors.toList());

        if (seatIds.isEmpty()) {
            return List.of();
        }

        return repository.findSeatsByIds(seatIds);
    }
}
