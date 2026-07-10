package com.maaitlunghau.__rest_api_jpa_mysql.exception;

// Extends RuntimeException (unchecked) — Spring chỉ rollback @Transactional khi gặp
// unchecked exception. Nếu dùng checked Exception, phải khai báo throws ở mọi nơi,
// rất verbose và không cần thiết cho REST API.
// (Extends RuntimeException: Spring auto-rollbacks on unchecked exceptions;
// no need to declare throws everywhere)
public class ResourceNotFoundException extends RuntimeException {

    // Nhận tên resource và id để tạo message chuẩn, thống nhất toàn app.
    // Ví dụ: "Product not found with id: 5"
    // (Takes resource name + id to produce a consistent message across the app)
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }
}
