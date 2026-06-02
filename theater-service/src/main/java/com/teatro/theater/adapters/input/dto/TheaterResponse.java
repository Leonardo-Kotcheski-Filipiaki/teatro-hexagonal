package com.teatro.theater.adapters.input.dto;

import com.teatro.theater.domain.model.Theater;

public record TheaterResponse(
        Long id,
        String name,
        String address,
        String city,
        Integer capacity
) {

    public static TheaterResponse fromDomain(Theater theater) {
        return new TheaterResponse(theater.getId(), theater.getName(), theater.getAddress(), theater.getCity(), theater.getCapacity());
    }
}
