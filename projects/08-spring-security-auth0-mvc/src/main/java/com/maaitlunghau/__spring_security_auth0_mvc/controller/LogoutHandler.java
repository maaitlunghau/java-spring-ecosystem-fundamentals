package com.maaitlunghau.__spring_security_auth0_mvc.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutHandler extends SecurityContextLogoutHandler {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public LogoutHandler(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);

        String issuer = (String) clientRegistrationRepository
            .findByRegistrationId("auth0")
            .getProviderDetails()
            .getConfigurationMetadata()
            .get("issuer");

        String clientId = clientRegistrationRepository
            .findByRegistrationId("auth0")
            .getClientId();

        String returnTo = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

        String logoutUrl = UriComponentsBuilder
            .fromHttpUrl(issuer + "/v2/logout?client_id={clientId}&returnTo={returnTo}")
            .encode()
            .buildAndExpand(clientId, returnTo)
            .toUriString();

        try {
            
            response.sendRedirect(logoutUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
