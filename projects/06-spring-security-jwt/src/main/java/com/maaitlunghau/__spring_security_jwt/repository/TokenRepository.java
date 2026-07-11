package com.maaitlunghau.__spring_security_jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maaitlunghau.__spring_security_jwt.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.isLoggedOut = false")
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    Optional<Token> findByToken(String token);
}
