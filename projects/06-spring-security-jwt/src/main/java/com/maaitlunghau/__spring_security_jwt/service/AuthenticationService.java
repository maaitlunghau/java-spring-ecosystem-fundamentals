package com.maaitlunghau.__spring_security_jwt.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.maaitlunghau.__spring_security_jwt.dto.AuthenticationResponse;
import com.maaitlunghau.__spring_security_jwt.model.Token;
import com.maaitlunghau.__spring_security_jwt.model.User;
import com.maaitlunghau.__spring_security_jwt.repository.TokenRepository;
import com.maaitlunghau.__spring_security_jwt.repository.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        TokenRepository tokenRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        User user = new User(
            request.getFirstName(),
            request.getLastName(),
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getRole()
        );
        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveToken(user, refreshToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        revokeAllUserTokens(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveToken(user, refreshToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow();

        boolean isValid = tokenRepository.findByToken(refreshToken)
            .map(t -> !t.isLoggedOut())
            .orElse(false);

        if (!isValid || !jwtService.isValid(refreshToken, user)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        return new AuthenticationResponse(newAccessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        tokenRepository.findByToken(refreshToken).ifPresent(t -> {
            t.setLoggedOut(true);
            tokenRepository.save(t);
        });
    }

    private void saveToken(User user, String refreshToken) {
        tokenRepository.save(new Token(refreshToken, user));
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        validTokens.forEach(t -> t.setLoggedOut(true));
        tokenRepository.saveAll(validTokens);
    }
}
