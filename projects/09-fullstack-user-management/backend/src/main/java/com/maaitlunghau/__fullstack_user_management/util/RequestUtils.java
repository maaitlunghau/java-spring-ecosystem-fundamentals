package com.maaitlunghau.__fullstack_user_management.util;

import jakarta.servlet.http.HttpServletRequest;

/** Trích IP + User-Agent từ request để bind thiết bị cho refresh token. Dùng chung nhiều controller. */
public final class RequestUtils {

    private RequestUtils() {}

    /** IP client thật: ưu tiên X-Forwarded-For (khi qua proxy/load balancer). */
    public static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
