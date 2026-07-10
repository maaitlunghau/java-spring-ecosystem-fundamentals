package com.maaitlunghau.__mvc_thymeleaf.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// MVC dùng @ControllerAdvice (không phải @RestControllerAdvice) — trả về tên view, không phải JSON.
// (MVC uses @ControllerAdvice: returns view name, not JSON response)
@ControllerAdvice
public class GlobalExceptionHandler {

    // Khi không tìm thấy resource, render trang error với message.
    // Model truyền message vào template để hiển thị cho user.
    // (Render error page with message when resource not found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    // Fallback — lỗi không mong đợi, hiển thị thông báo chung.
    // (Catch-all: show generic error page without exposing internal details)
    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return "error";
    }
}
