package com.maaitlunghau.__fullstack_user_management.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.maaitlunghau.__fullstack_user_management.security.CustomAccessDeniedHandler;
import com.maaitlunghau.__fullstack_user_management.security.CustomAuthenticationEntryPoint;
import com.maaitlunghau.__fullstack_user_management.security.JwtAuthenticationFilter;
import com.maaitlunghau.__fullstack_user_management.security.OAuth2LoginSuccessHandler;

/**
 * Cấu hình bảo mật.
 *
 * - JWT stateless cho API: mỗi request tự xác thực qua JwtAuthenticationFilter.
 * - @EnableMethodSecurity bật @PreAuthorize (phân quyền /api/users ở method-level).
 * - OAuth2 login (Auth0) chỉ bật KHI đã cấu hình client (Phase 3). Chưa cấu hình → app
 *   vẫn chạy bình thường với Phase 1-2.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Chỉ bật OAuth2 login khi có ClientRegistrationRepository (tức đã cấu hình Auth0)
        boolean oauth2Enabled = clientRegistrationRepository.getIfAvailable() != null;

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())   // dùng CorsConfigurationSource bean (CorsConfig)
            // OAuth2 authorization code cần lưu tạm state/nonce qua các redirect → IF_REQUIRED.
            // Khi chưa bật OAuth2 thì giữ STATELESS thuần cho API.
            .sessionManagement(s -> s.sessionCreationPolicy(
                oauth2Enabled ? SessionCreationPolicy.IF_REQUIRED : SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",                       // đăng ký / login / refresh / verify / exchange
                    "/oauth2/**", "/login/**",            // flow OAuth2 khởi tạo + callback
                    "/swagger-ui/**", "/v3/api-docs/**"   // API docs (khi thêm springdoc)
                ).permitAll()
                // /api/users/** phân quyền ở method-level bằng @PreAuthorize (xem UserController)
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint)   // 401
                .accessDeniedHandler(accessDeniedHandler)             // 403
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (oauth2Enabled) {
            // Sau khi Auth0 xác thực social → success handler phát code, không tạo session đăng nhập
            http.oauth2Login(oauth -> oauth.successHandler(oAuth2LoginSuccessHandler));
        }

        return http.build();
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
