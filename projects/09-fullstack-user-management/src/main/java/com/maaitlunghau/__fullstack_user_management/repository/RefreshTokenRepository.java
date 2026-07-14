package com.maaitlunghau.__fullstack_user_management.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maaitlunghau.__fullstack_user_management.entity.RefreshToken;
import com.maaitlunghau.__fullstack_user_management.entity.RevokedReason;
import com.maaitlunghau.__fullstack_user_management.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /** Tra token khi refresh — nhận vào là SHA-256(token), KHÔNG phải token gốc. */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Các phiên đang hoạt động của user — phục vụ UI "thiết bị đang đăng nhập". */
    List<RefreshToken> findByUserAndIsRevokedFalse(User user);

    /**
     * Thu hồi toàn bộ token còn hiệu lực của MỘT PHIÊN (theo sessionId).
     * Dùng cho reuse detection: token cũ bị dùng lại → giết cả họ token cùng session.
     *
     * clearAutomatically/flushAutomatically: bulk update đi thẳng xuống DB, không qua
     * persistence context → cần flush thay đổi đang chờ trước, và xoá context sau để
     * lần đọc kế tiếp không dính entity cũ (stale).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken rt
        SET rt.isRevoked = true, rt.revokedReason = :reason, rt.revokedAt = :now
        WHERE rt.sessionId = :sessionId AND rt.isRevoked = false
        """)
    int revokeAllActiveBySession(@Param("sessionId") String sessionId,
                                 @Param("reason") RevokedReason reason,
                                 @Param("now") Instant now);

    /**
     * Thu hồi toàn bộ token còn hiệu lực của MỘT USER (mọi phiên).
     * Dùng khi đổi/reset mật khẩu, admin thu hồi, logout-everywhere.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken rt
        SET rt.isRevoked = true, rt.revokedReason = :reason, rt.revokedAt = :now
        WHERE rt.user = :user AND rt.isRevoked = false
        """)
    int revokeAllActiveByUser(@Param("user") User user,
                              @Param("reason") RevokedReason reason,
                              @Param("now") Instant now);
}
