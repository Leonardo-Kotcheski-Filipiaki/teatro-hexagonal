package com.teatro.auth.adapters.output.mysql;

import com.teatro.auth.adapters.output.mysql.entity.UserEntity;
import com.teatro.auth.adapters.output.mysql.mapper.UserMapper;
import com.teatro.auth.adapters.output.mysql.repository.SpringDataUserRepository;
import com.teatro.auth.domain.model.User;
import com.teatro.auth.ports.output.UserRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserMysqlAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    public UserMysqlAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }


    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity savedEntity = repository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    @Cacheable(value = "users", key = "'list'")
    public List<User> findAllUsers() {
        return repository.findAll().stream().map(UserMapper::toDomain).toList();
    }
}
