package com.maaitlunghau.__spring_security_jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.maaitlunghau.__spring_security_jwt.filter.JwtAuthenticationFilter;
import com.maaitlunghau.__spring_security_jwt.service.UserDetailsServiceImpl;
import com.maaitlunghau.__spring_security_jwt.config.CustomAuthenticationEntryPoint;
import com.maaitlunghau.__spring_security_jwt.config.CustomAccessDeniedHandler;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                req->req
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            ).userDetailsService(userDetailsServiceImpl)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }   

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
