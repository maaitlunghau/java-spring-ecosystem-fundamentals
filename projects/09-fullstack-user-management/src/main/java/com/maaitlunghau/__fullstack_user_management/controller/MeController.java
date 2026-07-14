package com.maaitlunghau.__fullstack_user_management.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;
import com.maaitlunghau.__fullstack_user_management.dto.request.UpdateUserRequest;
import com.maaitlunghau.__fullstack_user_management.dto.response.UserResponse;
import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.service.UserService;

import jakarta.validation.Valid;

/**
 * Endpoint self-service cho chính người dùng đang đăng nhập (mọi role).
 * @AuthenticationPrincipal inject thẳng entity User vì nó implements UserDetails
 * và JwtAuthenticationFilter đã set nó làm principal.
 */
@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(UserResponse.from(user));
    }

    @PatchMapping
    public ApiResponse<UserResponse> update(@AuthenticationPrincipal User user,
                                            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse updated = UserResponse.from(userService.updateUser(user.getId(), request));
        return ApiResponse.ok("Cập nhật profile thành công", updated);
    }
}
