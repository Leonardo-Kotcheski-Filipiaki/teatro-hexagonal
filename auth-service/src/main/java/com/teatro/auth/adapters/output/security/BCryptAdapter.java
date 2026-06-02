package com.teatro.auth.adapters.output.security;

import com.teatro.auth.ports.output.PasswordEncryptionPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptAdapter implements PasswordEncryptionPort {
    private final PasswordEncoder passwordEncoder;

    public BCryptAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
