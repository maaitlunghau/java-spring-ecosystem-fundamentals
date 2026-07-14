package com.maaitlunghau.__fullstack_user_management.dto.request;

/**
 * Body của logout — refreshToken tùy chọn.
 * Có refreshToken → thu hồi đúng phiên hiện tại; không có → chỉ blacklist access token.
 */
public record LogoutRequest(String refreshToken) {}
