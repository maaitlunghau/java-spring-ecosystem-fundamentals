package com.maaitlunghau.__fullstack_user_management.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Trả về 403 khi request ĐÃ xác thực (biết là ai) nhưng KHÔNG đủ quyền
 * (vd USER gọi endpoint chỉ dành cho ADMIN).
 *
 * Phân biệt với 401: 401 = "chưa biết bạn là ai", 403 = "biết rồi nhưng không cho phép".
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
            ApiResponse.message(403, "Access denied — insufficient role"));
    }
}
