package com.teatro.event.adapters.input.dto;

public record TheaterSyncRequest(
        Long theaterId,
        Integer capacity
) {
}
