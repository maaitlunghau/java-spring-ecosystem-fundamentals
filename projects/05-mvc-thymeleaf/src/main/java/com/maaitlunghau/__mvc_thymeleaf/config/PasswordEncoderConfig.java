package com.maaitlunghau.__mvc_thymeleaf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    // Khai báo BCryptPasswordEncoder là một Bean để inject vào Service.
    // strength = 12 (mặc định = 10): số vòng hash — càng cao càng chậm và bảo mật hơn,
    // nhưng 12 là balance tốt giữa bảo mật và performance.
    // (BCrypt strength 12: good balance between security and performance; default is 10)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
