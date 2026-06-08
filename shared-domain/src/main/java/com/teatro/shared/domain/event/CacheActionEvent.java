package com.teatro.shared.domain.event;

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
