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
 * Token dùng-một-lần gửi qua link email — xác thực email hoặc đặt lại mật khẩu.
 *
 * Thiết kế bảo mật:
 *  - KHÔNG lưu token gốc, chỉ lưu SHA-256(token) → DB lộ không reset được mật khẩu người khác.
 *  - Đánh dấu đã dùng (isUsed) thay vì xóa → giữ audit + chống dùng lại token.
 *
 * Khác RefreshToken: đây là token dùng-một-lần trong link email, KHÔNG phải phiên đăng
 * nhập dài, nên không cần sessionId / rotation / device binding.
 */
@Entity
@Table(name = "verification_tokens", indexes = {
    @Index(name = "idx_verification_token_hash", columnList = "token_hash"),
    @Index(name = "idx_verification_user", columnList = "user_id")
})
public class VerificationToken {

    /** Phân biệt mục đích token — dùng chung một bảng. */
    public enum Type {
        EMAIL_VERIFY,     // kích hoạt tài khoản sau khi đăng ký
        PASSWORD_RESET    // đặt lại mật khẩu khi quên
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * SHA-256(token) dạng hex 64 ký tự — KHÔNG lưu token gốc.
     * Token PASSWORD_RESET đổi được mật khẩu bất kỳ ai, nên phải hash như password.
     * Lúc verify: hash token trong link rồi tra theo hash.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    // @SoftDelete trên User buộc quan hệ to-one phải EAGER (Hibernate không cho LAZY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Type type;

    /** Đánh dấu đã dùng thay vì xóa → giữ audit + chống dùng lại. */
    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    /** Mốc token ĐƯỢC dùng (quá khứ) — null nếu chưa dùng. */
    @Column(name = "used_at")
    private Instant usedAt;

    /** Mốc token ĐƯỢC tạo (quá khứ) — set tự động ở @PrePersist. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Mốc token SẼ hết hạn (tương lai) — expiresAt, không phải expiredAt. */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    protected VerificationToken() {}

    public VerificationToken(String tokenHash, User user, Type type, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.type = type;
        this.expiresAt = expiresAt;
    }

    // ===== domain methods =====

    /** Đánh dấu token đã dùng (ghi lại thời điểm để audit). */
    public void markUsed() {
        this.isUsed = true;
        this.usedAt = Instant.now();
    }

    /** true nếu đã quá hạn. */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /** true nếu còn hợp lệ để verify/reset (chưa dùng VÀ chưa hết hạn). */
    public boolean isUsable() {
        return !isUsed && !isExpired();
    }

    // ===== getters =====
    public Long getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public User getUser() {
        return user;
    }

    public Type getType() {
        return type;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
