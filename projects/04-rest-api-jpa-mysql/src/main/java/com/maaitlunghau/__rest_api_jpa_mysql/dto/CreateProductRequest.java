package com.maaitlunghau.__rest_api_jpa_mysql.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// DTO nhận từ client khi tạo mới — không có productId vì ID do DB generate.
// Validation đặt ở đây (không phải trên Entity) — đây là ranh giới hệ thống,
// nơi dữ liệu từ bên ngoài đi vào, cần validate trước khi xử lý.
// (Request DTO for create: no id field, validation lives here not on Entity)
public record CreateProductRequest(

        @NotBlank(message = "Product name is required")
        String productName,

        @Min(value = 0, message = "Price must be greater than or equal to 0")
        double productPrice,

        @Min(value = 0, message = "Quantity must be greater than or equal to 0")
        int quantity
) {}
