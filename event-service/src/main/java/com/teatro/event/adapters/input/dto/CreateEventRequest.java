package com.teatro.event.adapters.input.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotNull(message = "Precisa informar um teatro para o evento.")
        Long theaterId,
        @NotBlank(message = "Precisa informar um nome para o evento.")
        String name,
        @NotNull(message = "Precisa informar a data para o evento.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime eventDate,
        @NotNull(message = "Precisa informar o número total de assentos.")
        @Positive(message = "O número de assentos precisa ser maior que zero.")
        Integer totalSeats
        ) {
}
