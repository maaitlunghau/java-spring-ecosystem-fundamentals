package com.maaitlunghau.__fullstack_user_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /** Tra token khi verify/reset — nhận vào là SHA-256(token), KHÔNG phải token gốc. */
    Optional<VerificationToken> findByTokenHash(String tokenHash);

    /** Token cùng loại còn hiệu lực của user — vô hiệu trước khi phát token mới (chỉ link mới nhất dùng được). */
    List<VerificationToken> findByUserAndTypeAndIsUsedFalse(User user, VerificationToken.Type type);
}
