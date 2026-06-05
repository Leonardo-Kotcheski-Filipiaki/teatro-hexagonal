package com.teatro.reservation.adapters.output.mysql.repository;

import com.teatro.reservation.adapters.output.mysql.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByEventIdAndUserId(Long eventId, Long userId);

    List<BookingEntity> findByUserId(Long userId);
}
