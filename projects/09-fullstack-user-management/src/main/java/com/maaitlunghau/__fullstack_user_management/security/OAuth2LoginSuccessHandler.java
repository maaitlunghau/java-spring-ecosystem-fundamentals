package com.maaitlunghau.__fullstack_user_management.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.maaitlunghau.__fullstack_user_management.entity.User;
import com.maaitlunghau.__fullstack_user_management.service.OneTimeCodeStore;
import com.maaitlunghau.__fullstack_user_management.service.SocialLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring gọi handler này sau khi Auth0 xác thực social thành công.
 * Lấy danh tính từ OidcUser → link/tạo User nội bộ → sinh one-time code (map tới userId)
 * → redirect về FE kèm code. FE sẽ POST code để đổi lấy JWT nội bộ (xem OAuth2ExchangeController).
 *
 * KHÔNG phát token ở đây — token phát lúc exchange để không lộ trên URL và giữ token khỏi Redis.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SocialLoginService socialLoginService;
    private final OneTimeCodeStore oneTimeCodeStore;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(SocialLoginService socialLoginService,
                                     OneTimeCodeStore oneTimeCodeStore,
                                     @Value("${app.frontend-url}") String frontendUrl) {
        this.socialLoginService = socialLoginService;
        this.oneTimeCodeStore = oneTimeCodeStore;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OidcUser oidc = (OidcUser) authentication.getPrincipal();

        String sub = oidc.getSubject();                 // vd "google-oauth2|1234567890"
        String provider = sub.contains("|") ? sub.substring(0, sub.indexOf('|')) : "auth0";
        String email = oidc.getEmail();
        boolean emailVerified = Boolean.TRUE.equals(oidc.getEmailVerified());
        String fullName = oidc.getFullName();
        String avatar = oidc.getPicture();

        User user = socialLoginService.findOrCreate(provider, sub, email, emailVerified, fullName, avatar);
        String code = oneTimeCodeStore.issue(user.getId());

        String redirect = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth/callback")
            .queryParam("code", code)
            .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirect);
    }
}
