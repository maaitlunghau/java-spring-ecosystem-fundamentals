package com.maaitlunghau.__fullstack_user_management.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Sau callback OAuth2, không thể trả JWT thẳng cho SPA (redirect là GET của browser,
 * để token trên URL rất không an toàn). Giải pháp: sinh một code ngẫu nhiên dùng một lần,
 * lưu Redis (TTL 60s) map tới userId, redirect FE kèm code; FE POST code để đổi lấy JWT.
 *
 * Cố ý CHỈ lưu userId (không lưu token) — token phát lúc exchange, giữ nguyên nguyên tắc
 * không để refresh token thô nằm trong Redis.
 */
@Service
public class OneTimeCodeStore {

    private static final String PREFIX = "otc:";
    private static final Duration TTL = Duration.ofSeconds(60);

    private final StringRedisTemplate redis;

    public OneTimeCodeStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** Sinh code cho userId, trả code để redirect về FE. */
    public String issue(Long userId) {
        String code = UUID.randomUUID().toString();
        redis.opsForValue().set(PREFIX + code, userId.toString(), TTL);
        return code;
    }

    /** Đổi code lấy userId — GETDEL nguyên tử (dùng một lần). null nếu không hợp lệ/hết hạn. */
    public Long consume(String code) {
        String value = redis.opsForValue().getAndDelete(PREFIX + code);
        return value == null ? null : Long.valueOf(value);
    }
}
