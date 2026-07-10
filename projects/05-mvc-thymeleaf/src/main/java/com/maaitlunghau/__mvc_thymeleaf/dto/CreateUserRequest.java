package com.maaitlunghau.__mvc_thymeleaf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO nhận từ form Thymeleaf khi tạo user mới — không có id (DB generate).
// Validation đặt ở đây, không phải trên Entity.
// (Create DTO: form data for new user, no id field, validation lives here)
public record CreateUserRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        // minLength = 6 để đảm bảo password không quá ngắn trước khi hash.
        // (Minimum length check before hashing — BCrypt works on any length but short passwords are weak)
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 150, message = "Age must be at most 150")
        int age
) {}
