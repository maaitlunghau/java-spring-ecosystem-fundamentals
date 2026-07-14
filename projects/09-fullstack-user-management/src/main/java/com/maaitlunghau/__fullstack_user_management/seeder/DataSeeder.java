package com.maaitlunghau.__fullstack_user_management.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.maaitlunghau.__fullstack_user_management.entity.Role;
import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@usermanagement.dev";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                "Administrator",                     
                adminEmail,                           
                passwordEncoder.encode("112233"),   
                Role.ADMIN,                           
                true,                                 
                true
            );
            userRepository.save(admin);
        }
    }
}
