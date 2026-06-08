package com.teatro.event.adapters.output.mysql;

import com.teatro.event.adapters.output.mysql.entity.EventEntity;
import com.teatro.event.adapters.output.mysql.mapper.EventMapper;
import com.teatro.event.adapters.output.mysql.repository.SpringDataEventRepository;
import com.teatro.event.domain.model.Event;
import com.teatro.event.ports.output.EventRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventMysqlAdapter implements EventRepositoryPort {

    private final SpringDataEventRepository repository;

    public EventMysqlAdapter(SpringDataEventRepository repository) {
        this.repository = repository;
    }


    @Override
    public Event save(Event event) {
        EventEntity entity = EventMapper.toEntity(event);
        EventEntity saved = repository.save(entity);
        return EventMapper.toDomain(saved);
    }

    @Override
    @Cacheable(value = "events", key = "'list'")
    public List<Event> list() {
        return repository.findAll().stream().map(EventMapper::toDomain).toList();
    }

    @Cacheable(value = "events", key = "#id")
    @Override
    public Optional<Event> listId(Long id) {
        return repository.findById(id)
                .map(EventMapper::toDomain);
    }
}
