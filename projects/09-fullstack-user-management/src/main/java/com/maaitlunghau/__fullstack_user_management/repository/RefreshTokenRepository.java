package com.maaitlunghau.__fullstack_user_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maaitlunghau.__fullstack_user_management.entity.RefreshToken;
import com.maaitlunghau.__fullstack_user_management.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /** Tra token khi refresh — nhận vào là SHA-256(token), KHÔNG phải token gốc. */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Các phiên đang hoạt động của user — dùng cho UI "thiết bị đăng nhập" + logout-everywhere. */
    List<RefreshToken> findByUserAndIsRevokedFalse(User user);

    /** Các token còn hiệu lực của MỘT PHIÊN — dùng cho reuse detection. */
    List<RefreshToken> findBySessionIdAndIsRevokedFalse(String sessionId);
}
