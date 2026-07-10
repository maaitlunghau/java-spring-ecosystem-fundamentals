package com.maaitlunghau.__rest_api_jpa_mysql.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // Bắt lỗi validation từ @Valid — khi request body vi phạm constraint (@NotBlank, @Min...).
    // Gom tất cả field errors thành một message duy nhất, trả 400 Bad Request.
    // (Handles @Valid failures: collects all field errors into one message, returns 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "message", errors
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
