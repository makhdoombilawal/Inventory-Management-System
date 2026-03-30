package com.inventoryapp.common.ratelimiter;
 
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
 
/**
 * Service for managing API rate limits using Redis.
 * Supports per-tenant and per-user rate limiting.
 */
@Service
@Slf4j
public class RateLimiterService {
    
    private final StringRedisTemplate redisTemplate;
    
    public RateLimiterService(ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
    }
    
    @Value("${rate-limiting.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Value("${rate-limiting.enabled:true}")
    private boolean enabled;
    
    /**
     * Check if a request is allowed based on the rate limit
     * 
     * @param key The unique key for rate limiting (e.g., tenant:user:endpoint)
     * @return true if the request is allowed, false if it's rate-limited
     */
    public boolean isAllowed(String key) {
        if (!enabled) {
            return true;
        }
        
        try {
            if (redisTemplate == null) {
                log.warn("StringRedisTemplate bean not available. Rate limiting is disabled.");
                return true;
            }
            String redisKey = "ratelimit:" + key;
            Long count = redisTemplate.opsForValue().increment(redisKey);
            
            if (count != null && count == 1) {
                // Set TTL of 1 minute for the new key
                redisTemplate.expire(redisKey, 1, TimeUnit.MINUTES);
            }
            
            if (count != null && count > requestsPerMinute) {
                log.warn("Rate limit exceeded for key: {}. Count: {}", key, count);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error checking rate limit in Redis", e);
            // Fail open in case of Redis errors to ensure availability
            return true;
        }
    }
}
