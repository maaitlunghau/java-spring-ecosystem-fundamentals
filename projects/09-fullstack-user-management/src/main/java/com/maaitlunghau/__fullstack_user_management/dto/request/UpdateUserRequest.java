package com.maaitlunghau.__fullstack_user_management.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest (
    @NotBlank String fullName,
    String avatarUrl
) {}
