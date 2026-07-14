package com.maaitlunghau.__fullstack_user_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maaitlunghau.__fullstack_user_management.dto.request.CreateUserRequest;
import com.maaitlunghau.__fullstack_user_management.dto.request.UpdateUserRequest;
import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.exception.DuplicateResourceException;
import com.maaitlunghau.__fullstack_user_management.exception.ResourceNotFoundException;
import com.maaitlunghau.__fullstack_user_management.repository.UserRepository;
import com.maaitlunghau.__fullstack_user_management.spec.UserSpecs;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> search(String keyword, String role, Boolean enabled, Pageable pagable) {
        Specification<User> spec = Specification
            .allOf(
                UserSpecs.search(keyword),
                UserSpecs.hasRole(role),
                UserSpecs.enable(enabled)
            );
        
        return userRepository.findAll(spec, pagable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email đã tồn tại: " + request.email());
        }

        User user = new User(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.fullName()
        );

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = this.findById(id);
        user.updateProfile(request.fullName(), request.avatarUrl());

        return user;
    }   

    @Transactional
    public void deleteUser(Long id) {
        User user = this.findById(id);
        userRepository.delete(user);
    }
}
