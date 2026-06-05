package com.teatro.reservation.adapters.output.mysql.mapper;

import com.teatro.reservation.adapters.output.mysql.entity.SeatEntity;
import com.teatro.reservation.domain.model.Seat;

public class SeatMapper {

    // 🔄 BANCO ➡️ DOMÍNIO
    public static Seat toDomain(SeatEntity entity) {
        if (entity == null) return null;

        // Usa o construtor completo do seu modelo de domínio Seat
        return new Seat(
                entity.getId(),
                entity.getEventId(),
                entity.getSeatCode(),
                entity.getStatus(),
                entity.getReservedAt()
        );
    }

    // 🔄 DOMÍNIO ➡️ BANCO
    public static SeatEntity toEntity(Seat domain) {
        if (domain == null) return null;

        SeatEntity entity = new SeatEntity();
        entity.setId(domain.getId());
        entity.setEventId(domain.getEventId());
        entity.setSeatCode(domain.getSeatCode());
        entity.setStatus(domain.getStatus());
        entity.setReservedAt(domain.getReservedAt());

        return entity;
    }
}