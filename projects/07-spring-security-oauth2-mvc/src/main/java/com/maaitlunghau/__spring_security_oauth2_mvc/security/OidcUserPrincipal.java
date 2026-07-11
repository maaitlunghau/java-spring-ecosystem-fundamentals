package com.maaitlunghau.__spring_security_oauth2_mvc.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;

// Extend DefaultOidcUser để kế thừa toàn bộ OIDC methods (getSubject, getEmail, getPicture...)
// Chỉ thêm getUser() để controller truy cập được User entity từ SecurityContext
public class OidcUserPrincipal extends DefaultOidcUser implements UserAware {

    private final User user;

    public OidcUserPrincipal(User user, OidcUser oidcUser) {
        super(
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo()
        );
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
