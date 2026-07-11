package com.maaitlunghau.__spring_security_jwt.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklist {

    private static final String PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklist(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String jti, Duration ttl) {
        redisTemplate.opsForValue().set(PREFIX + jti, "1", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }
}
