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

    // Trả về raw User entity để controller có thể bind vào form update.
    // (Returns entity for form binding in update view)
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

        // Hash password trước khi lưu — không bao giờ lưu plain text vào DB.
        // passwordEncoder.encode() tự tạo random salt và nhúng vào trong chuỗi hash,
        // nên cùng một password sẽ ra hash khác nhau mỗi lần — an toàn hơn.
        // (Hash before saving: BCrypt auto-generates and embeds salt, same password → different hash each time)
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

        // Nếu password mới không trống thì hash và update, ngược lại giữ nguyên password cũ.
        // StringUtils.hasText() kiểm tra không null và không blank.
        // (Only re-hash and update password if a new one was provided)
        if (StringUtils.hasText(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        // Không cần gọi save() — Hibernate dirty checking tự detect thay đổi và flush khi commit.
        // (No explicit save(): Hibernate dirty checking handles flush on transaction commit)
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
