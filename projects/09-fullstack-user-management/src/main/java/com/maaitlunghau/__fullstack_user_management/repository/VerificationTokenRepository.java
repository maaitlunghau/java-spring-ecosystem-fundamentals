package com.maaitlunghau.__fullstack_user_management.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /** Tra token khi verify/reset — nhận vào là SHA-256(token), KHÔNG phải token gốc. */
    Optional<VerificationToken> findByTokenHash(String tokenHash);

    /**
     * Vô hiệu hoá mọi token cùng loại còn hiệu lực của user trước khi phát token mới,
     * để CHỈ link mới nhất còn dùng được (vd: user bấm "quên mật khẩu" nhiều lần).
     *
     * clearAutomatically/flushAutomatically: xem giải thích ở RefreshTokenRepository.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE VerificationToken vt
        SET vt.isUsed = true, vt.usedAt = :now
        WHERE vt.user = :user AND vt.type = :type AND vt.isUsed = false
        """)
    int markAllActiveAsUsed(@Param("user") User user,
                            @Param("type") VerificationToken.Type type,
                            @Param("now") Instant now);
}
