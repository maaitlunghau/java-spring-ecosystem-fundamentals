package com.maaitlunghau.__spring_security_jwt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private boolean isLoggedOut = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Token() {}

    public Token(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public boolean isLoggedOut() { return isLoggedOut; }
    public User getUser() { return user; }

    public void setLoggedOut(boolean loggedOut) {
        isLoggedOut = loggedOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
