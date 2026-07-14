package com.maaitlunghau.__fullstack_user_management.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maaitlunghau.__fullstack_user_management.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Xử lý 2 loại token trong hệ thống:
 *
 *  1) ACCESS TOKEN = JWT (stateless, mang sẵn claim) — client gửi kèm mỗi request.
 *     Có jti để blacklist khi logout. Sống ngắn (15 phút).
 *
 *  2) REFRESH / VERIFICATION TOKEN = chuỗi opaque ngẫu nhiên (KHÔNG phải JWT).
 *     Vì các token này luôn được tra trong DB, JWT không thêm giá trị mà còn lộ claim.
 *     Chỉ lưu SHA-256(token) trong DB → dùng generateOpaqueToken() phát, hashToken() để lưu/tra.
 *
 * (Ở codebase lớn, phần opaque + hash có thể tách sang một TokenHasher riêng cho đúng SRP —
 *  ở đây gộp chung cho gọn vì cùng là "thao tác token".)
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpiration;             // ms
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") long accessExpiration) {
        // HMAC-SHA key từ secret; secret phải đủ dài (>= 32 byte cho HS256)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
    }

    // ================= ACCESS TOKEN (JWT) =================

    /** Phát access token JWT: subject = email, kèm jti (blacklist) + role. */
    public String generateAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
            .subject(user.getEmail())
            .id(UUID.randomUUID().toString())          // jti — để blacklist khi logout
            .claim("role", user.getRole().name())
            .issuedAt(now)
            .expiration(new Date(now.getTime() + accessExpiration))
            .signWith(key)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** TTL còn lại (giây) của access token — để set TTL blacklist trong Redis. */
    public long remainingSeconds(String token) {
        long millis = extractExpiration(token).getTime() - System.currentTimeMillis();
        return Math.max(0, Duration.ofMillis(millis).toSeconds());
    }

    /** Token hợp lệ nếu đúng chủ sở hữu (email) và chưa hết hạn. */
    public boolean isAccessTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /** Parse + verify chữ ký; token hỏng/hết hạn/sai chữ ký sẽ ném JwtException. */
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return resolver.apply(claims);
    }

    // ============ OPAQUE TOKEN (refresh / verification) ============

    /**
     * Sinh token opaque ngẫu nhiên 256-bit, mã hoá base64url — đây là giá trị TRẢ CHO
     * client (qua response hoặc link email). KHÔNG lưu thẳng chuỗi này vào DB.
     */
    public String generateOpaqueToken() {
        byte[] bytes = new byte[32];                 // 32 byte = 256-bit entropy
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * SHA-256(token) dạng hex 64 ký tự — hash token opaque TRƯỚC KHI lưu hoặc tra cứu.
     * Token opaque có entropy cao (không brute-force như password) nên chỉ cần hash
     * nhanh 1 chiều, không cần BCrypt.
     */
    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 luôn có trong mọi JVM → không bao giờ xảy ra
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
