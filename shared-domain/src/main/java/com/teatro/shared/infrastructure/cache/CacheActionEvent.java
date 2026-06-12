package com.teatro.shared.infrastructure.cache;

import com.teatro.shared.domain.event.DomainEvent;

public record CacheActionEvent(
        String cacheName,
        String key,
        Object value,
        CacheAction action
) implements DomainEvent {

    public enum CacheAction {
        PUT, EVICT
    }
}
