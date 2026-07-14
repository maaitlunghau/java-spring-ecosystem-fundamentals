package com.maaitlunghau.__fullstack_user_management.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.maaitlunghau.__fullstack_user_management.repository.UserRepository;

/**
 * Cầu nối giữa Spring Security và DB user.
 *
 * Spring Security gọi loadUserByUsername() khi cần xác thực (vd lúc login qua
 * AuthenticationManager). Vì User đã implements UserDetails nên trả thẳng entity,
 * không cần lớp adapter. "username" ở đây là email (User.getUsername() trả email).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
