package com.teatro.shared.infrastructure.cache;

import com.teatro.shared.domain.event.CacheActionEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class GlobalRedisCacheListener {

    private final CacheManager cacheManager;

    public GlobalRedisCacheListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheAction(CacheActionEvent event) {
        Cache cache = cacheManager.getCache(event.cacheName());

        if (cache != null) {
            if (event.action() == CacheActionEvent.CacheAction.EVICT) {
                cache.evict(event.key());
            } else if (event.action() == CacheActionEvent.CacheAction.PUT) {
                cache.put(event.key(), event.value());
            }
        }
    }
}