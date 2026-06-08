package com.teatro.event.domain.model;

public class Theater {
    private Long theaterId;
    private Integer capacity;

    public Long getTheaterId() {
        return theaterId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Theater() {}

    public Theater(Long theaterId, Integer capacity) {
        this.theaterId = theaterId;
        this.capacity = capacity;
    }
}
