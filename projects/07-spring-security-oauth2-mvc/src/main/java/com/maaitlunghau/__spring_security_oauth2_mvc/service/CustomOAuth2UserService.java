package com.maaitlunghau.__spring_security_oauth2_mvc.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.AuthProvider;
import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;
import com.maaitlunghau.__spring_security_oauth2_mvc.repository.UserRepository;
import com.maaitlunghau.__spring_security_oauth2_mvc.security.OAuth2UserPrincipal;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        // GitHub trả về: id (Integer), login (username), email (nullable), avatar_url
        String providerId = oAuth2User.getAttribute("id").toString();
        String name = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");

        // GitHub email có thể null nếu user để chế độ private
        String email = oAuth2User.getAttribute("email");
        if (email == null) email = "";

        User user = saveOrUpdate(providerId, email, name, avatarUrl);
        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(String providerId, String email, String name, String avatarUrl) {
        return userRepository.findByProviderAndProviderId(AuthProvider.GITHUB, providerId)
            .map(existing -> {
                existing.updateProfile(name, avatarUrl);
                return userRepository.save(existing);
            })
            .orElseGet(() -> userRepository.save(
                new User(email, name, avatarUrl, AuthProvider.GITHUB, providerId)
            ));
    }
}
