package com.teatro.theater.adapters.output.mysql.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "theater")
public class TheaterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int capacity;

    public TheaterEntity(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public static TheaterEntity create(String name, String address, String city, int capacity) {
        TheaterEntity t = new TheaterEntity();
        t.name = name;
        t.address = address;
        t.city = city;
        t.capacity = capacity;

        return t;
    }
}
