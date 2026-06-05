package com.teatro.event.adapters.output.mysql.mapper;

import com.teatro.event.adapters.output.mysql.entity.TheaterEntity;
import com.teatro.event.domain.model.Theater;

public class TheaterMapper {
    public static Theater toDomain(TheaterEntity entity) {
        if (entity == null) return null;
        return new Theater(entity.getTheaterId(), entity.getCapacity());
    }

    public static TheaterEntity toEntity(Theater domain) {
        if (domain == null) return null;
        TheaterEntity entity = new TheaterEntity();
        entity.setTheaterId(domain.getTheaterId());
        entity.setCapacity(domain.getCapacity());
        return entity;
    }
}
