package com.inventoryapp.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = true)
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(@NonNull String key, @NonNull Object value, long timeout, @NonNull TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        log.debug("Cached: {}", key);
    }

    public Object get(@NonNull String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @CacheEvict(value = {"dealers", "vehicles", "admin"}, allEntries = true)
    public void evictAll() {
        log.info("Evicted all caches");
    }

    @CacheEvict(value = "dealers", key = "#tenantId")
    public void evictDealerCache(Long tenantId) {
        log.debug("Evicted dealer cache for tenant: {}", tenantId);
    }

    @CacheEvict(value = "vehicles", key = "#tenantId")
    public void evictVehicleCache(Long tenantId) {
        log.debug("Evicted vehicle cache for tenant: {}", tenantId);
    }
}
