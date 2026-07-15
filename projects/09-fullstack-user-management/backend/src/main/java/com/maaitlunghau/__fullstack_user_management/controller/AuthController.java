package com.maaitlunghau.__fullstack_user_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;
import com.maaitlunghau.__fullstack_user_management.dto.request.ForgotPasswordRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.LoginRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.LogoutRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.RefreshRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.RegisterRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.ResetPasswordRequest;
import com.maaitlunghau.__fullstack_user_management.dto.response.AuthResponse;
import com.maaitlunghau.__fullstack_user_management.service.AuthService;
import com.maaitlunghau.__fullstack_user_management.util.CookieUtils;
import com.maaitlunghau.__fullstack_user_management.util.RequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.message(201, "Đăng ký thành công. Kiểm tra email để kích hoạt."));
    }

    @GetMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ApiResponse.message(200, "Xác thực email thành công");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                           HttpServletRequest servletRequest,
                                           HttpServletResponse response) {
        AuthResponse tokens = authService.login(request,
            RequestUtils.clientIp(servletRequest), RequestUtils.userAgent(servletRequest));
        CookieUtils.setAuthCookies(response, tokens);
        return ApiResponse.ok("Đăng nhập thành công", tokens);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refresh(@RequestBody(required = false) RefreshRequest request,
                                             HttpServletRequest servletRequest,
                                             HttpServletResponse response) {
        String refreshToken = request != null ? request.refreshToken() : null;
        if (refreshToken == null) {
            refreshToken = CookieUtils.readCookie(servletRequest, "refresh_token");
        }
        AuthResponse tokens = authService.refresh(refreshToken,
            RequestUtils.clientIp(servletRequest), RequestUtils.userAgent(servletRequest));
        CookieUtils.setAuthCookies(response, tokens);
        return ApiResponse.ok("Cấp access token mới", tokens);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) LogoutRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse response) {

        String accessToken = null;
        if (authHeader != null) {
            accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        }
        if (accessToken == null) {
            accessToken = CookieUtils.readCookie(servletRequest, "access_token");
        }

        String refreshToken = request != null ? request.refreshToken() : null;
        if (refreshToken == null) {
            refreshToken = CookieUtils.readCookie(servletRequest, "refresh_token");
        }

        if (accessToken == null) {
            // Không token để blacklist (client đã mất cookie) — vẫn xóa cookie, logout idempotent
            CookieUtils.clearAuthCookies(response);
            return ApiResponse.message(200, "Đăng xuất thành công");
        }

        authService.logout(accessToken, refreshToken);
        CookieUtils.clearAuthCookies(response);
        return ApiResponse.message(200, "Đăng xuất thành công");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ApiResponse.message(200, "Nếu email tồn tại, link đặt lại đã được gửi.");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.message(200, "Đặt lại mật khẩu thành công");
    }
}
