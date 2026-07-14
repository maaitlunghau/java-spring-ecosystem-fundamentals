package com.maaitlunghau.__fullstack_user_management.dto.response;

import java.time.LocalDateTime;

import com.maaitlunghau.__fullstack_user_management.entity.User;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    String avatarUrl,
    boolean isEmailVerified,
    boolean isEnabled,
    String role,
    LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getAvatarUrl(),
            user.isEmailVerified(),
            user.isEnabled(),
            user.getRole().name(),
            user.getCreatedAt()
        );
    }
}
