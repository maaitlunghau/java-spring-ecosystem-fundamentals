package com.maaitlunghau.__spring_security_jwt.dto;

import com.maaitlunghau.__spring_security_jwt.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username,
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotNull(message = "Role is required") Role role
) {}
