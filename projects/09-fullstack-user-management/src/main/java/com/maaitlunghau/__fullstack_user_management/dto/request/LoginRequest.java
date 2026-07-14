package com.maaitlunghau.__fullstack_user_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Đăng nhập — không đặt @Size cho password để không lộ policy khi login. */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
