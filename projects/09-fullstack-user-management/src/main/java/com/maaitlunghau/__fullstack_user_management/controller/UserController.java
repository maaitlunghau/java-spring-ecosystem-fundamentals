package com.maaitlunghau.__fullstack_user_management.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maaitlunghau.__fullstack_user_management.dto.ApiResponse;
import com.maaitlunghau.__fullstack_user_management.dto.request.CreateUserRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.UpdateUserRequest;
import com.maaitlunghau.__fullstack_user_management.dto.response.UserResponse;
import com.maaitlunghau.__fullstack_user_management.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> list(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) Boolean enabled,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pagable) 
    {
        Page<UserResponse> page = userService.search(keyword, role, enabled, pagable)
            .map(UserResponse::from);
        return ApiResponse.ok(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(UserResponse.from(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = UserResponse.from(userService.createUser(request));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("User created", created));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
        @PathVariable Long id, 
        @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse updated = UserResponse.from(userService.updateUser(id, request));
        return ApiResponse.ok("User updated", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.message(200, "User soft-deleted");
    }
}
