package com.teatro.reservation.adapters.output.mysql.repository;

import com.teatro.reservation.adapters.output.mysql.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
}
