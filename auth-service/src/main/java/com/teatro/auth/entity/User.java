package com.teatro.auth.entity;

import jakarta.persistence.*;
import com.teatro.auth.enums.Roles;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role = Roles.CUSTOMER;

    @Column(nullable = false)
    private boolean active = true;

    // factory method — senha já deve chegar hasheada
    public static User create(String name, String email,
                              String passwordHash, Roles role) {
        User u = new User();
        u.name         = name;
        u.email        = email;
        u.passwordHash = passwordHash;
        u.role         = role;
        return u;
    }

}

