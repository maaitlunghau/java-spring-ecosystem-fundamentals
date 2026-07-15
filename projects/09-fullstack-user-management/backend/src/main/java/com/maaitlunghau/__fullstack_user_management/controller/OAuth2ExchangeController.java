package com.maaitlunghau.__fullstack_user_management.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;
import com.maaitlunghau.__fullstack_user_management.dto.response.AuthResponse;
import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.exception.BadRequestException;
import com.maaitlunghau.__fullstack_user_management.repository.UserRepository;
import com.maaitlunghau.__fullstack_user_management.service.AuthService;
import com.maaitlunghau.__fullstack_user_management.service.OneTimeCodeStore;
import com.maaitlunghau.__fullstack_user_management.util.CookieUtils;
import com.maaitlunghau.__fullstack_user_management.util.RequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FE gọi endpoint này với one-time code nhận từ URL callback → đổi lấy JWT nội bộ.
 * Token được phát Ở ĐÂY (không phải lúc success handler) → bind đúng thiết bị đang gọi API.
 */
@RestController
@RequestMapping("/api/auth")
public class OAuth2ExchangeController {

    private final OneTimeCodeStore oneTimeCodeStore;
    private final UserRepository userRepository;
    private final AuthService authService;

    public OAuth2ExchangeController(OneTimeCodeStore oneTimeCodeStore,
                                    UserRepository userRepository,
                                    AuthService authService) {
        this.oneTimeCodeStore = oneTimeCodeStore;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/oauth2/exchange")
    public ApiResponse<AuthResponse> exchange(@RequestParam String code, HttpServletRequest request,
                                              HttpServletResponse response) {
        Long userId = oneTimeCodeStore.consume(code);
        if (userId == null) {
            throw new BadRequestException("Code không hợp lệ hoặc đã hết hạn");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User không tồn tại"));

        AuthResponse tokens = authService.issueNewSession(
            user, RequestUtils.clientIp(request), RequestUtils.userAgent(request));
        CookieUtils.setAuthCookies(response, tokens);
        return ApiResponse.ok("Social login thành công", tokens);
    }
}
