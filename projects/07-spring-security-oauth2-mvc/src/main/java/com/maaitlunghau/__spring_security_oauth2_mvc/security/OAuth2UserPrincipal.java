package com.maaitlunghau.__spring_security_oauth2_mvc.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;

public class OAuth2UserPrincipal implements OAuth2User, UserAware {

    private final User user;
    private final Map<String, Object> attributes;

    public OAuth2UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    // getName() là định danh duy nhất của principal trong Spring Security
    @Override
    public String getName() {
        return user.getProviderId();
    }

    public User getUser() {
        return user;
    }
}
