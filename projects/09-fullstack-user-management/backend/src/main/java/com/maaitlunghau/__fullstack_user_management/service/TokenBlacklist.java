package com.maaitlunghau.__fullstack_user_management.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Thu hồi access token — tầng 1 (Redis).
 *
 * Access token là JWT stateless, không tự thu hồi được (server không lưu trạng thái).
 * Khi logout, ta chặn theo jti: ghi jti vào Redis với TTL = thời gian access token còn
 * sống. Filter check jti trong blacklist trước khi cho qua. TTL hết → Redis tự xoá key,
 * không cần dọn thủ công (đúng lúc token cũng đã hết hạn nên không cần chặn nữa).
 *
 * Chỉ lưu String -> String nên dùng thẳng StringRedisTemplate auto-config, KHÔNG cần
 * RedisConfig riêng.
 */
@Service
public class TokenBlacklist {

    // Namespace key trong Redis để tránh đụng các key khác (one-time code, rate limit...)
    private static final String PREFIX = "blacklist:jti:";

    private final StringRedisTemplate redis;

    public TokenBlacklist(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** Chặn một jti với TTL = số giây access token còn sống → Redis tự dọn khi hết hạn. */
    public void blacklist(String jti, long ttlSeconds) {
        if (jti != null && ttlSeconds > 0) {
            redis.opsForValue().set(PREFIX + jti, "1", Duration.ofSeconds(ttlSeconds));
        }
    }

    /** true nếu jti đang bị chặn (token đã logout nhưng chưa hết hạn tự nhiên). */
    public boolean isBlacklisted(String jti) {
        return jti != null && Boolean.TRUE.equals(redis.hasKey(PREFIX + jti));
    }
}
