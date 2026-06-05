package com.teatro.reservation.adapters.output.mysql.repository;

import com.teatro.reservation.adapters.output.mysql.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
}
