package com.maaitlunghau.__fullstack_user_management.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Chặn brute-force các endpoint auth nhạy cảm (login/register/forgot-password):
 * token bucket cho mỗi (IP + path), hết token → 429.
 *
 * In-memory (ConcurrentHashMap) đủ cho single instance / học tập. Chạy nhiều instance
 * sau load balancer thì thay bằng Bucket4j + Redis để chia sẻ bucket.
 */
@Component
@Order(1)   // chạy sớm, trước khi vào controller
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** 5 request / phút / IP cho mỗi endpoint. */
    private Bucket newBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build())
            .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.equals("/api/auth/login")
              || path.equals("/api/auth/register")
              || path.equals("/api/auth/forgot-password"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String key = request.getRemoteAddr() + ":" + request.getRequestURI();
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            objectMapper.writeValue(response.getWriter(),
                ApiResponse.message(429, "Quá nhiều yêu cầu. Thử lại sau 1 phút."));
        }
    }
}
