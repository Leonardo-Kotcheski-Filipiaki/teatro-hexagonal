package com.teatro.theater.adapters.input.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTheaterRequest(
        @NotBlank(message = "O nome não pode ser vazio.")
        String name,
        @NotBlank(message = "O endereço não pode ser vazio.")
        String address,
        @NotBlank(message = "A cidade não pode ser vazio.")
        String city,
        @NotNull(message = "A capacidade deve ser informada.")
        @Min(value = 1, message = "A capacidade deve ser maior que 1")
        Integer capacity

) {
}
