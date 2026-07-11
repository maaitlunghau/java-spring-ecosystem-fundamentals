package com.maaitlunghau.__spring_security_oauth2_mvc.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.AuthProvider;
import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;
import com.maaitlunghau.__spring_security_oauth2_mvc.repository.UserRepository;
import com.maaitlunghau.__spring_security_oauth2_mvc.security.OidcUserPrincipal;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(request);

        // Google OIDC claims: sub (unique user ID), email, name, picture
        String providerId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String avatarUrl = oidcUser.getPicture();

        User user = saveOrUpdate(providerId, email, name, avatarUrl);
        return new OidcUserPrincipal(user, oidcUser);
    }

    private User saveOrUpdate(String providerId, String email, String name, String avatarUrl) {
        return userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
            .map(existing -> {
                existing.updateProfile(name, avatarUrl);
                return userRepository.save(existing);
            })
            .orElseGet(() -> userRepository.save(
                new User(email, name, avatarUrl, AuthProvider.GOOGLE, providerId)
            ));
    }
}
