package com.maaitlunghau.__spring_security_oauth2_mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.maaitlunghau.__spring_security_oauth2_mvc.handler.OAuth2LoginSuccessHandler;
import com.maaitlunghau.__spring_security_oauth2_mvc.service.CustomOAuth2UserService;
import com.maaitlunghau.__spring_security_oauth2_mvc.service.CustomOidcUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2LoginSuccessHandler successHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomOidcUserService customOidcUserService,
                          OAuth2LoginSuccessHandler successHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // CSRF bật — MVC app dùng form/session nên cần bảo vệ CSRF (khác REST API)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)   // GitHub
                    .oidcUserService(customOidcUserService) // Google
                )
                .successHandler(successHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            // Session fixation protection — tạo session mới sau khi login
            .sessionManagement(session -> session
                .sessionFixation().newSession()
                .maximumSessions(1)
            )
            .build();
    }
}
