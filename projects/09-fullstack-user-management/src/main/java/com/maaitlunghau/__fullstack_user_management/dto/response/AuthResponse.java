package com.maaitlunghau.__fullstack_user_management.dto.response;

/**
 * Kết quả login/refresh — theo dạng token response kiểu OAuth2:
 *  - accessToken: JWT gửi kèm mỗi request.
 *  - refreshToken: chuỗi opaque để xin access token mới.
 *  - tokenType: luôn "Bearer" (client set header "Authorization: Bearer <accessToken>").
 *  - expiresIn: số giây access token còn sống → FE biết khi nào cần refresh chủ động.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresInSeconds) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds);
    }
}
