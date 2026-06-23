package com.teatro.auth.domain.model;

import com.teatro.shared.domain.enums.Role;

public class User {

    private Long id;

    private String name;

    private String email;

    private String passwordhash;

    private Role role;

    public User(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public Role getRole() {
        return role;
    }

    public User(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordhash = passwordHash;
        this.role = role;
    }

    public User(Long id, String name, String email, String passwordHash, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordhash = passwordHash;
        this.role = role;
    }
}
