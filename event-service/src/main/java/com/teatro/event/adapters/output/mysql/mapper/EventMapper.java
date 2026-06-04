package com.teatro.event.adapters.output.mysql.mapper;

import com.teatro.event.adapters.output.mysql.entity.EventEntity;
import com.teatro.event.domain.model.Event;

public class EventMapper {
    public static Event toDomain(EventEntity entity) {
        if (entity == null) return null;
        return new Event(entity.getId(), entity.getTheaterId(), entity.getName(), entity.getEventDate(), entity.getTotalSeats(), entity.getAvailableSeats(), entity.getStatus());
    }

    public static EventEntity toEntity(Event domain) {
        if (domain == null) return null;
        EventEntity entity = new EventEntity();
        entity.setId(domain.getId());
        entity.setTheaterId(domain.getTheaterId());
        entity.setName(domain.getName());
        entity.setEventDate(domain.getEventDate());
        entity.setTotalSeats(domain.getTotalSeats());
        entity.setAvailableSeats(domain.getAvailableSeats());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}
