package com.maaitlunghau.__fullstack_user_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.maaitlunghau.__fullstack_user_management.security.CustomAccessDeniedHandler;
import com.maaitlunghau.__fullstack_user_management.security.CustomAuthenticationEntryPoint;
import com.maaitlunghau.__fullstack_user_management.security.JwtAuthenticationFilter;

/**
 * Cấu hình bảo mật Phase 2 — thay bản permit-all tạm ở Phase 1.
 *
 * Stateless JWT: không session, mỗi request tự xác thực qua JwtAuthenticationFilter.
 * @EnableMethodSecurity bật @PreAuthorize để phân quyền ở tầng method khi cần.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // Token ở header (không dùng cookie session) → CSRF không áp dụng
            .csrf(AbstractHttpConfigurer::disable)
            // Không tạo/dùng HttpSession — mỗi request tự xác thực bằng token
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",                       // đăng ký / login / refresh / verify
                    "/swagger-ui/**", "/v3/api-docs/**"   // API docs (khi thêm springdoc)
                ).permitAll()
                // /api/users/** phân quyền ở method-level bằng @PreAuthorize (xem UserController)
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint)   // 401
                .accessDeniedHandler(accessDeniedHandler)             // 403
            )
            // Chạy JWT filter TRƯỚC filter username/password mặc định
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager cho AuthService.login() gọi authenticate().
     * Spring Boot tự dựng DaoAuthenticationProvider từ UserDetailsServiceImpl + PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
