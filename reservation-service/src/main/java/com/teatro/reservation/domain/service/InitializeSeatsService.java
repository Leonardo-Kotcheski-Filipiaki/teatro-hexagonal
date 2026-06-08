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
        System.out.println(listaGerada);
        repository.saveAllSeats(listaGerada);
    }
}
