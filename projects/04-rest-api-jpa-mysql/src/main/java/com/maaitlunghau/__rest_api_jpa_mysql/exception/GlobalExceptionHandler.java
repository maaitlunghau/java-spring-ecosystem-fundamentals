package com.maaitlunghau.__rest_api_jpa_mysql.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice — interceptor toàn cục, bắt exception từ tất cả @RestController
// trong app mà không cần try-catch trong từng controller.
// = @ControllerAdvice + @ResponseBody (tự convert return value sang JSON)
// (Global interceptor: catches exceptions from all @RestController without try-catch in each one)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler chỉ định loại exception method này xử lý.
    // Khi bất kỳ controller nào throw ResourceNotFoundException,
    // Spring sẽ gọi method này thay vì trả về Whitelabel Error Page.
    // (Handles ResourceNotFoundException thrown from any controller in the app)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "message", ex.getMessage()
                ));
    }

    // Fallback — bắt tất cả exception không được handle ở trên.
    // Trả 500 Internal Server Error thay vì để lộ stack trace ra client.
    // (Catch-all fallback: returns 500 instead of exposing stack trace to client)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", 500,
                        "message", "Internal server error"
                ));
    }
}
