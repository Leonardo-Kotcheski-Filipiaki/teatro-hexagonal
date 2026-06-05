package com.teatro.theater.adapters.input.dto;

public record TheaterSyncRequest (
        Long theaterId,
        Integer capacity
) {
}
