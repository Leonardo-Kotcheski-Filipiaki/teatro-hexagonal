package com.teatro.reservation.domain.service;

import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.input.InitializeSeatsUseCase;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public class InitializeSeatsService implements InitializeSeatsUseCase {

    private final ReservationRepositoryPort repository;

    public InitializeSeatsService(ReservationRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Long eventId, int totalSeats) {
        int seatsPerRow = totalSeats / 10;

        if (totalSeats < 50 || seatsPerRow < 5) {
            seatsPerRow = 5;
        }

        List<Seat> listaGerada = new ArrayList<>();
        char rowLetter = 'A';
        int seatNumber = 1;

        for (int i = 1; i <= totalSeats; i++) {

            String seatCode = rowLetter + "-" + seatNumber;

            Seat novoAssento = new Seat(eventId, seatCode);
            listaGerada.add(novoAssento);

            seatNumber++;

            if (seatNumber > seatsPerRow) {
                seatNumber = 1;
                rowLetter++;
            }
        }

        System.out.println(listaGerada);
        repository.saveAllSeats(listaGerada);
    }
}
