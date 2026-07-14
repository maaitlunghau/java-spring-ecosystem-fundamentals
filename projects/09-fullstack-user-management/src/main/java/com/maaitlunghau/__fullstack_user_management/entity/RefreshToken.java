package com.maaitlunghau.__fullstack_user_management.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Refresh token — token sống lâu (7 ngày), chỉ dùng để xin access token mới.
 *
 * Thiết kế bảo mật production-grade:
 *  - KHÔNG lưu token gốc, chỉ lưu SHA-256(token) → DB lộ cũng không dùng được.
 *  - Token rotation: mỗi lần refresh cấp token mới, thu hồi token cũ (dùng 1 lần).
 *  - Reuse detection: token đã revoked mà bị dùng lại = bị đánh cắp → thu hồi cả session.
 *  - Audit: lưu vì sao/khi nào bị thu hồi + IP + thiết bị để điều tra & quản lý phiên.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token_hash", columnList = "token_hash"),
    @Index(name = "idx_refresh_user", columnList = "user_id"),
    @Index(name = "idx_refresh_session", columnList = "session_id")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * SHA-256(token) dạng hex 64 ký tự — KHÔNG lưu token gốc.
     * Refresh token mạnh ngang password (đổi được thành access token), nên phải
     * hash như password. Lúc refresh: hash token client gửi lên rồi tra theo hash.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * ID nhóm mọi token thuộc CÙNG một phiên đăng nhập (sinh lúc login, giữ nguyên
     * qua các lần rotation). Khi phát hiện token bị đánh cắp → thu hồi cả họ token
     * theo sessionId này, thay vì chỉ 1 token lẻ.
     */
    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revoked_reason", length = 30)
    private RevokedReason revokedReason;

    /**
     * Vết rotation: hash của token đã THAY THẾ token này. Dùng để lần theo chuỗi
     * rotation (token cũ → token mới → token mới hơn...) khi điều tra.
     */
    @Column(name = "replaced_by_token_hash", length = 64)
    private String replacedByTokenHash;

    /** IP lúc cấp token — phát hiện bất thường (token cấp ở nơi này, dùng ở nơi khác). */
    @Column(name = "ip_address", length = 45)   // 45 ký tự đủ chứa cả IPv6
    private String ipAddress;

    /** Trình duyệt/thiết bị — phục vụ UI "các thiết bị đang đăng nhập". */
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Mốc token SẼ hết hạn (tương lai). Tên "expiresAt" (không phải "expiredAt")
     * vì đây là hạn chót trong tương lai, không phải sự kiện đã xảy ra.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    protected RefreshToken() {}

    public RefreshToken(String tokenHash, User user, String sessionId,
                        Instant expiresAt, String ipAddress, String userAgent) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.sessionId = sessionId;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }


    public void revoke(RevokedReason reason) {
        this.isRevoked = true;
        this.revokedReason = reason;
        this.revokedAt = Instant.now();
    }

    /** Đánh dấu token này đã bị thay bằng token mới (khi rotation lúc refresh). */
    public void rotatedTo(String newTokenHash) {
        this.replacedByTokenHash = newTokenHash;
        revoke(RevokedReason.ROTATED);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !isRevoked && !isExpired();
    }

    public Long getId() {
        return id;
    }
    public String getTokenHash() {
        return tokenHash;
    }
    public User getUser() {
        return user;
    }
    public String getSessionId() {
        return sessionId;
    }
    public boolean isRevoked() {
        return isRevoked;
    }
    public Instant getRevokedAt() {
        return revokedAt;
    }
    public RevokedReason getRevokedReason() {
        return revokedReason;
    }
    public String getReplacedByTokenHash() {
        return replacedByTokenHash;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getExpiresAt() {
        return expiresAt;
    }
}
