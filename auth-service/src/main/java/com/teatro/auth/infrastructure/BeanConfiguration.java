package com.teatro.auth.infrastructure;

import com.teatro.auth.domain.service.CreateUserService;
import com.teatro.auth.domain.service.FindAllUsersService;
import com.teatro.auth.domain.service.LoginService;
import com.teatro.auth.ports.input.CreateUserCase;
import com.teatro.auth.ports.input.FindAllUsersUseCase;
import com.teatro.auth.ports.output.PasswordEncryptionPort;
import com.teatro.auth.ports.output.TokenServicePort;
import com.teatro.auth.ports.output.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateUserCase createUserCase(UserRepositoryPort userRepositoryPort) {
        return new CreateUserService(userRepositoryPort);
    }

    @Bean
    public LoginService loginService(UserRepositoryPort userRepositoryPort,
                                     PasswordEncryptionPort passwordEncryptionPort,
                                     TokenServicePort tokenServicePort) {
        return new LoginService(userRepositoryPort, passwordEncryptionPort, tokenServicePort);
    }

    @Bean
    public FindAllUsersUseCase findAllUsersUseCase(UserRepositoryPort userRepositoryPort) {
        return new FindAllUsersService(userRepositoryPort);
    }
}
