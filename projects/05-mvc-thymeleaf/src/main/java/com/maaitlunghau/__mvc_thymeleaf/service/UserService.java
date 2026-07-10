package com.maaitlunghau.__mvc_thymeleaf.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.maaitlunghau.__mvc_thymeleaf.dto.CreateUserRequest;
import com.maaitlunghau.__mvc_thymeleaf.dto.UpdateUserRequest;
import com.maaitlunghau.__mvc_thymeleaf.dto.UserResponse;
import com.maaitlunghau.__mvc_thymeleaf.exception.ResourceNotFoundException;
import com.maaitlunghau.__mvc_thymeleaf.model.User;
import com.maaitlunghau.__mvc_thymeleaf.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.email(), hashedPassword, request.age());
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setAge(request.age());

        if (StringUtils.hasText(request.password())) {
            if (request.password().length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(request.password()));
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
