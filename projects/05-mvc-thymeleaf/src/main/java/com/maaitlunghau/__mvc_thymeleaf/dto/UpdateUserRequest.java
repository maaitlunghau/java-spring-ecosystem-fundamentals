package com.maaitlunghau.__mvc_thymeleaf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO nhận từ form khi update — không có id (lấy từ URL path), không bắt buộc đổi password.
// password để trống = giữ nguyên password cũ.
// (Update DTO: id from URL, empty password means keep existing)
public record UpdateUserRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        // Không @NotBlank — cho phép để trống để giữ password cũ.
        // Nếu có giá trị thì phải đủ 6 ký tự.
        // (Optional: blank = keep existing password; if provided must meet minimum length)
        @Size(min = 6, message = "New password must be at least 6 characters")
        String password,

        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 150, message = "Age must be at most 150")
        int age
) {}
