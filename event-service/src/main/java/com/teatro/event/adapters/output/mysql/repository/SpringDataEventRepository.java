package com.teatro.event.adapters.output.mysql.repository;

import com.teatro.event.adapters.output.mysql.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEventRepository extends JpaRepository<EventEntity, Long> {
}
