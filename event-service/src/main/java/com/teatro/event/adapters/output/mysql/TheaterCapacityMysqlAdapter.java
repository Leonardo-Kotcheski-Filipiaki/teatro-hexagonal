package com.teatro.event.adapters.output.mysql;

import com.teatro.event.adapters.output.mysql.entity.TheaterEntity;
import com.teatro.event.adapters.output.mysql.mapper.TheaterMapper;
import com.teatro.event.adapters.output.mysql.repository.SpringDataTheaterCapacityRepository;
import com.teatro.event.domain.model.Theater;
import com.teatro.event.ports.output.TheaterCapacityRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


@Component
public class TheaterCapacityMysqlAdapter implements TheaterCapacityRepositoryPort {

    private final SpringDataTheaterCapacityRepository repository;

    public TheaterCapacityMysqlAdapter(SpringDataTheaterCapacityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Theater save(Theater theater) {
        TheaterEntity entity = TheaterMapper.toEntity(theater);
        TheaterEntity saved = repository.save(entity);
        return TheaterMapper.toDomain(saved);
    }

    @Override
    @Cacheable(value = "theater_capacity", key = "#theaterId")
    public Theater getCapacity(Long theaterId) {
        return repository.findById(theaterId).map(TheaterMapper::toDomain).orElseThrow(() -> new IllegalArgumentException("Teatro não encontrado!"));
    }
}
