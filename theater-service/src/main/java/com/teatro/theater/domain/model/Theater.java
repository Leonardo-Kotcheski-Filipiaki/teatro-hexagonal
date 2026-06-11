package com.teatro.theater.domain.model;

public class Theater {

    private Long id;

    private String name;

    private String address;

    private String city;

    private Integer capacity;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Theater() {}

    public Theater(String name, String address, String city, Integer capacity) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.capacity = capacity;
    }

    public Theater(Long id, String name, String address, String city, Integer capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.capacity = capacity;
    }
}
