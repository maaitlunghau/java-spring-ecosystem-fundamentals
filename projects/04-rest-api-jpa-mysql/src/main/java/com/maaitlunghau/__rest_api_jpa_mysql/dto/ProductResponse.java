package com.maaitlunghau.__rest_api_jpa_mysql.dto;

import com.maaitlunghau.__rest_api_jpa_mysql.model.Product;

// DTO trả về client — chứa tất cả field kể cả id đã được generate.
// Dùng Java Record: immutable, tự generate constructor/getters/equals/hashCode.
// Tách khỏi Entity để sau này thêm field nhạy cảm vào Entity không bị lộ ra ngoài.
// (Response DTO: exposes only what client needs, decouples API shape from DB schema)
public record ProductResponse(
        Long productId,
        String productName,
        double productPrice,
        int quantity
) {
    // Static factory method — convert từ Entity sang DTO ở một chỗ duy nhất,
    // tránh lặp lại mapping logic ở nhiều nơi.
    // (Static factory: single place to map Entity → DTO)
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice(),
                product.getQuantity()
        );
    }
}
