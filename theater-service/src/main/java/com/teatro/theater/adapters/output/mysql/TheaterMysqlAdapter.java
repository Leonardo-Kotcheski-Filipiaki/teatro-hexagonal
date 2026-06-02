package com.teatro.theater.adapters.output.mysql;

import com.teatro.theater.adapters.output.mysql.entity.TheaterEntity;
import com.teatro.theater.adapters.output.mysql.mapper.TheaterMapper;
import com.teatro.theater.adapters.output.mysql.repository.SpringDataTheaterRepository;
import com.teatro.theater.domain.model.Theater;
import com.teatro.theater.ports.output.TheaterRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class TheaterMysqlAdapter implements TheaterRepositoryPort {
    private final SpringDataTheaterRepository repository;

    public TheaterMysqlAdapter(SpringDataTheaterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Theater save(Theater theater) {
        TheaterEntity entity = TheaterMapper.toEntity(theater);
        TheaterEntity saved = repository.save(entity);
        return TheaterMapper.toDomain(saved);
    }
}
