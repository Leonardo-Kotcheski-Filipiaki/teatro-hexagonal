package com.teatro.reservation.adapters.output.mysql.mapper;

import com.teatro.reservation.adapters.output.mysql.entity.BookingEntity;
import com.teatro.reservation.adapters.output.mysql.entity.SeatEntity;
import com.teatro.reservation.domain.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking toDomain(BookingEntity entity) {
        if (entity == null) return null;
        List<Long> seatsId = List.of();
        if(entity.getSeats() != null) {
            seatsId = entity.getSeats().stream()
                    .map(SeatEntity::getId)
                    .collect(Collectors.toList());
        }

        return new Booking(
                entity.getId(),
                entity.getEventId(),
                entity.getUserId(),
                entity.getStatus(),
                entity.getBookedAt(),
                seatsId
        );
    }

    public static BookingEntity toEntity(Booking domain) {
        if (domain == null) return null;

        BookingEntity entity = BookingEntity.create(
                domain.getId(),
                domain.getEventId(),
                domain.getUserId(),
                domain.getStatus(),
                domain.getBookedAt()
        );

        if (domain.getSeatIds() != null) {
            List<SeatEntity> seatEntities = domain.getSeatIds().stream()
                    .map(id -> {
                        SeatEntity seat = new SeatEntity();
                        seat.setId(id);
                        return seat;
                    })
                    .collect(Collectors.toList());
            entity.setSeats(seatEntities);
        }

        return entity;
    }
}
