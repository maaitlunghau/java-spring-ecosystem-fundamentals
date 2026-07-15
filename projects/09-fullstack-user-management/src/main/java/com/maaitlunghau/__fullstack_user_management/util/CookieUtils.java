package com.maaitlunghau.__fullstack_user_management.util;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.maaitlunghau.__fullstack_user_management.dto.response.AuthResponse;

import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtils {

    private CookieUtils() {}

    public static void setAuthCookies(HttpServletResponse response, AuthResponse tokens) {
        ResponseCookie access = ResponseCookie.from("access_token", tokens.accessToken())
            .httpOnly(true).secure(false)
            .path("/").sameSite("Lax")
            .maxAge(Duration.ofSeconds(tokens.expiresIn()))
            .build();

        ResponseCookie refresh = ResponseCookie.from("refresh_token", tokens.refreshToken())
            .httpOnly(true).secure(false)
            .path("/api/auth/refresh-token").sameSite("Lax")
            .maxAge(Duration.ofDays(7))
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    public static void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie access = ResponseCookie.from("access_token", "")
            .httpOnly(true).path("/").maxAge(0).build();
        ResponseCookie refresh = ResponseCookie.from("refresh_token", "")
            .httpOnly(true).path("/api/auth/refresh-token").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }
}
