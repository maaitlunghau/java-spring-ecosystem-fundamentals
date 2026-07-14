package com.maaitlunghau.__fullstack_user_management.spec;

import com.maaitlunghau.__fullstack_user_management.entity.Role;
import com.maaitlunghau.__fullstack_user_management.entity.User;

import org.springframework.data.jpa.domain.Specification;

public final class UserSpecs {

    private UserSpecs() {}

    public static Specification<User> search(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                cb.like(cb.lower(root.get("email")), pattern),
                cb.like(cb.lower(root.get("fullName")), pattern)
            );
        };
    }

    public static Specification<User> hasRole(String roleName) {
        return (root, query, cb) -> {
            if (roleName == null || roleName.isBlank()) return null;

            try {
                return cb.equal(root.get("role"), Role.valueOf(roleName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return cb.disjunction();
            }
        };
    }

    public static Specification<User> enable(Boolean isEnable) {
        return (root, query, cb) -> 
            isEnable == null ? null : cb.equal(root.get("isEnable"), isEnable);
    }

    public static Specification<User> emailVerified(Boolean isEmailVerified) {
        return (root, query, cb) -> 
            isEmailVerified == null ? null : cb.equal(root.get("isEmailVerified"), isEmailVerified);
    }
}
