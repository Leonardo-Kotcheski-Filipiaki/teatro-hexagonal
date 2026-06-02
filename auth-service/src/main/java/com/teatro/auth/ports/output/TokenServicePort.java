package com.teatro.auth.ports.output;

import com.teatro.auth.domain.model.User;

public interface TokenServicePort {

    String generateToken(User user);

}
