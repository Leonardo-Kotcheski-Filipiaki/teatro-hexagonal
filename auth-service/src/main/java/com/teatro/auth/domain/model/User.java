package com.teatro.auth.domain.model;

import com.teatro.shared.domain.enums.Role;

public class User {

    private Long id;

    private String name;

    private String email;

    private String passwordhash;

    private Role role;

    private boolean active = true;

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

    public boolean isActive() {
        return active;
    }

    public void desativar() {
        this.active = false;
    }

    public void ativar() {
        this.active = true;
    }

    public User(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordhash = passwordHash;
        this.role = role;
        this.active = true;
    }

    public User(Long id, String name, String email, String passwordHash, Role role, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordhash = passwordHash;
        this.role = role;
        this.active = active;
    }
}
