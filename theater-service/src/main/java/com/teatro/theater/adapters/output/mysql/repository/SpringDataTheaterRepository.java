package com.teatro.theater.adapters.output.mysql.repository;

import com.teatro.theater.adapters.output.mysql.entity.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTheaterRepository extends JpaRepository<TheaterEntity, Long> {


}
