package com.teatro.theater.adapters.output.mysql.mapper;

import com.teatro.theater.adapters.output.mysql.entity.TheaterEntity;
import com.teatro.theater.domain.model.Theater;

public class TheaterMapper {

    public static Theater toDomain(TheaterEntity entity) {
        if (entity == null) return null;
        return new Theater(entity.getId(), entity.getName(), entity.getAddress(), entity.getCity(), entity.getCapacity());
    }

    public static TheaterEntity toEntity(Theater domain) {
        if (domain == null) return null;
        TheaterEntity entity = new TheaterEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setAddress(domain.getAddress());
        entity.setCity(domain.getCity());
        entity.setCapacity(domain.getCapacity());
        return entity;
    }
}
