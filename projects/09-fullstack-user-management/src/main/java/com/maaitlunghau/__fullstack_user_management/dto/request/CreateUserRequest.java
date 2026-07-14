package com.maaitlunghau.__fullstack_user_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest (
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, message = "Password tối thiểu 6 ký tự") String password,
    @NotBlank String fullName
) {}
