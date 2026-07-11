package com.maaitlunghau.__spring_security_oauth2_mvc.security;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;

public interface UserAware {
    User getUser();
}
