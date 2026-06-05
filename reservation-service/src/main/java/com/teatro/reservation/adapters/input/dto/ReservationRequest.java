package com.teatro.reservation.adapters.input.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationRequest(
        @NotNull(message = "Deve ser selecionado um evento para realizar a reserva!")
        Long eventId,
        @NotNull(message = "Usuário não logado ou inexistênte!")
        Long userId,
        @NotEmpty(message = "Nenhum assento foi selecionado!")
        List<Long> seatIds
) {
}
