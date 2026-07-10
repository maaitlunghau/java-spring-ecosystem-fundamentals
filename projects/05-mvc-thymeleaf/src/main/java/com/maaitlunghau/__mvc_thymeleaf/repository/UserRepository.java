package com.maaitlunghau.__mvc_thymeleaf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maaitlunghau.__mvc_thymeleaf.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Kiểm tra trùng username/email trước khi tạo mới — dùng trong service để validate.
    // Spring Data tự generate query từ tên method, không cần viết SQL.
    // (Spring Data derives query from method name — no SQL needed)
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
