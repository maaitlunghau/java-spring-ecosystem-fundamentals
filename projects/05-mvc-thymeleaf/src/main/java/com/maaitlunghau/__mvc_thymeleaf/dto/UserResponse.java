package com.maaitlunghau.__mvc_thymeleaf.dto;

import com.maaitlunghau.__mvc_thymeleaf.model.User;

// DTO trả về view — không bao giờ expose password (dù đã hash) ra ngoài.
// (Response DTO: never expose password field, even hashed)
public record UserResponse(Long id, String username, String email, int age) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getAge());
    }
}
