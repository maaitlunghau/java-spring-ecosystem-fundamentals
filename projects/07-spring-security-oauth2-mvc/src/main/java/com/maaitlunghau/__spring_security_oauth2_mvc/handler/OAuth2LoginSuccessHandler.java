package com.maaitlunghau.__spring_security_oauth2_mvc.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public OAuth2LoginSuccessHandler() {
        // Fallback URL nếu không có saved request
        setDefaultTargetUrl("/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // SavedRequestAwareAuthenticationSuccessHandler tự động:
        // 1. Kiểm tra session xem user đang cố vào URL nào trước khi bị redirect sang login
        // 2. Redirect về URL đó nếu có → UX tốt hơn
        // 3. Fallback về /dashboard nếu không có saved request
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
