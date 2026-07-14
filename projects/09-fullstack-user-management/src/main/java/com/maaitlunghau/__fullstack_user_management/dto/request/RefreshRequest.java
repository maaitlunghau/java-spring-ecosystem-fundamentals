package com.maaitlunghau.__fullstack_user_management.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Xin access token mới — refreshToken là chuỗi opaque client nhận lúc login. */
public record RefreshRequest(@NotBlank String refreshToken) {}
