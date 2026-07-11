package com.maaitlunghau.__spring_security_oauth2_mvc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.AuthProvider;
import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
