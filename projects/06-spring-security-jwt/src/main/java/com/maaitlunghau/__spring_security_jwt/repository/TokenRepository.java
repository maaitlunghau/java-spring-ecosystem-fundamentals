package com.maaitlunghau.__spring_security_jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maaitlunghau.__spring_security_jwt.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUserIdAndIsLoggedOutFalse(Long userId);

    Optional<Token> findByToken(String token);
}
