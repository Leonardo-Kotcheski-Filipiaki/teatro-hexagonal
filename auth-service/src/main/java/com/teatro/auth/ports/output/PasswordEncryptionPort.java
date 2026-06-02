package com.teatro.auth.ports.output;

public interface PasswordEncryptionPort {
    boolean matches(String rawPassword, String encodedPassword);
}
