package com.teatro.auth.adapters.output.mysql.mapper;

import com.teatro.auth.adapters.output.mysql.entity.UserEntity;
import com.teatro.auth.domain.model.User;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.isActive()
        );
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) return null;
        UserEntity entity = new UserEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordhash());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());
        return entity;
    }
}
