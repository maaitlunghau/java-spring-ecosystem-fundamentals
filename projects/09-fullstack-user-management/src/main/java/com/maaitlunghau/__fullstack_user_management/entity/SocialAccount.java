package com.maaitlunghau.__fullstack_user_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Liên kết một danh tính social (provider + id bên provider) tới một User nội bộ.
 * Một User có thể có nhiều SocialAccount (đăng nhập bằng Google lẫn GitHub → cùng 1 user).
 */
@Entity
@Table(
    name = "social_accounts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @SoftDelete trên User buộc quan hệ to-one phải EAGER (Hibernate không cho LAZY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String provider;          // google, github, ... (parse từ Auth0 sub)

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;    // sub từ Auth0

    protected SocialAccount() {}

    public SocialAccount(User user, String provider, String providerUserId) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getProvider() { return provider; }
    public String getProviderUserId() { return providerUserId; }
}
