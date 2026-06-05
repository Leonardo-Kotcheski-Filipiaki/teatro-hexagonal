package com.teatro.reservation.adapters.output.mysql;

import com.teatro.reservation.adapters.output.mysql.entity.BookingEntity;
import com.teatro.reservation.adapters.output.mysql.mapper.BookingMapper;
import com.teatro.reservation.adapters.output.mysql.mapper.SeatMapper;
import com.teatro.reservation.adapters.output.mysql.repository.BookingJpaRepository;
import com.teatro.reservation.adapters.output.mysql.repository.SeatJpaRepository;
import com.teatro.reservation.domain.model.Booking;
import com.teatro.reservation.domain.model.Seat;
import com.teatro.reservation.ports.output.ReservationRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMysqlAdapter implements ReservationRepositoryPort {

    private final BookingJpaRepository bookingRepository;
    private final SeatJpaRepository seatRepository;

    public ReservationMysqlAdapter(BookingJpaRepository bookingRepository, SeatJpaRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }


    @Override
    public List<Seat> findSeatsByIds(List<Long> seatIds) {
        return seatRepository.findAllById(seatIds).stream()
                .map(SeatMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Booking saveBooking(Booking booking) {
        BookingEntity entity = BookingMapper.toEntity(booking);
        BookingEntity savedEntity = bookingRepository.save(entity);
        return BookingMapper.toDomain(savedEntity);
    }

    @Override
    public void saveAllSeats(List<Seat> seats) {
        var entities = seats.stream()
                .map(SeatMapper::toEntity)
                .collect(Collectors.toList());
        seatRepository.saveAll(entities);
    }
}
