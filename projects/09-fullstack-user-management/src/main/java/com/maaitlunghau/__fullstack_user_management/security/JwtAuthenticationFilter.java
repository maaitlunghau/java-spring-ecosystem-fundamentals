package com.maaitlunghau.__fullstack_user_management.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.maaitlunghau.__fullstack_user_management.service.JwtService;
import com.maaitlunghau.__fullstack_user_management.service.TokenBlacklist;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Chạy MỘT LẦN mỗi request (OncePerRequestFilter): đọc access token từ header
 * Authorization, xác thực, rồi nạp danh tính vào SecurityContext để các bước sau
 * (authorization, @PreAuthorize) biết user là ai.
 *
 * Filter KHÔNG tự trả 401/403. Nếu không xác thực được, nó chỉ để trống context và
 * cho request đi tiếp — SecurityConfig sẽ để EntryPoint (401) / AccessDeniedHandler (403)
 * quyết định. Đây là nguyên tắc tách bạch: filter xác thực, config phân quyền.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklist tokenBlacklist;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   TokenBlacklist tokenBlacklist,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);   // không có token → để endpoint/EntryPoint xử lý
            return;
        }

        String token = header.substring(7);   // bỏ "Bearer "
        try {
            String username = jwtService.extractUsername(token);
            String jti = jwtService.extractJti(token);

            // Chỉ xác thực khi: có username, context chưa có ai, và token chưa bị blacklist (logout)
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null
                    && !tokenBlacklist.isBlacklisted(jti)) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isAccessTokenValid(token, userDetails.getUsername())) {
                    var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            // token hỏng/hết hạn/sai chữ ký → không set context → EntryPoint trả 401
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
