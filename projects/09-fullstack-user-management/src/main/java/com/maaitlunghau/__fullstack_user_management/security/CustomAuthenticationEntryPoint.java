package com.maaitlunghau.__fullstack_user_management.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Trả về 401 khi request CHƯA xác thực mà truy cập tài nguyên cần đăng nhập
 * (không có token, hoặc token hỏng/hết hạn nên filter không set context).
 *
 * Inject ObjectMapper do Spring quản lý (không new thủ công) để JSON trả về — nhất là
 * field timestamp trong ApiResponse — serialize đồng nhất (ISO-8601) với phần còn lại của API.
 *
 * Boot 4 / Jackson 3: ObjectMapper nằm ở package tools.jackson.databind.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
            ApiResponse.message(401, "Authentication required"));
    }
}
