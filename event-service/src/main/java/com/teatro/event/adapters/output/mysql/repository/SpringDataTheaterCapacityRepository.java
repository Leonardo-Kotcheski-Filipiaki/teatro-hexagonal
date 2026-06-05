package com.teatro.event.adapters.output.mysql.repository;

import com.teatro.event.adapters.output.mysql.entity.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTheaterCapacityRepository extends JpaRepository<TheaterEntity, Long> {

}
