package com.maaitlunghau.__spring_security_oauth2_mvc.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    // ID định danh user bên phía provider (Google sub, GitHub id)
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected User() {}

    public User(String email, String name, String avatarUrl, AuthProvider provider, String providerId) {
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.role = Role.USER;
    }

    // Cập nhật profile mỗi lần user login lại — đảm bảo name/avatar luôn sync với provider
    public void updateProfile(String name, String avatarUrl) {
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public AuthProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User[id=%d, email=%s, provider=%s]".formatted(id, email, provider);
    }
}
