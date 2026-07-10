package com.maaitlunghau.__rest_api_jpa_mysql.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// DTO nhận từ client khi update — id lấy từ URL path, không nhận trong body.
// Tách Create và Update thành 2 record riêng vì về sau chúng thường khác nhau:
// ví dụ Update có thể cho phép partial update hoặc có field khác Create.
// (Separate Update DTO: id comes from URL path, body only contains updatable fields)
public record UpdateProductRequest(

        @NotBlank(message = "Product name is required")
        String productName,

        @Min(value = 0, message = "Price must be greater than or equal to 0")
        double productPrice,

        @Min(value = 0, message = "Quantity must be greater than or equal to 0")
        int quantity
) {}
