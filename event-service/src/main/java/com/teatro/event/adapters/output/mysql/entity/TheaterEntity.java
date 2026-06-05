package com.teatro.event.adapters.output.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "theater_capacity")
public class TheaterEntity {
    @Id
    @Column(name = "theater_id", nullable = false)
    private Long theaterId;

    @Column(nullable = false)
    Integer capacity;

    public TheaterEntity(){}

    public Long getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(Long theaterId) {
        this.theaterId = theaterId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public TheaterEntity(Long theaterId, Integer capacity) {
        this.theaterId = theaterId;
        this.capacity = capacity;
    }
}
