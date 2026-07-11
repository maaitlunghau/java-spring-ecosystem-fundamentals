package com.maaitlunghau.__spring_security_auth0_mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.AntPathMatcher;

import com.maaitlunghau.__spring_security_auth0_mvc.controller.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LogoutHandler logoutHandler;

    public SecurityConfig(LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/", "/js/**", "/css/**", "/images/**", "/error").permitAll()
                .anyRequest().authenticated())
            .logout(logout->logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .addLogoutHandler(logoutHandler)
            )
            .formLogin(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .build();
    }
}
