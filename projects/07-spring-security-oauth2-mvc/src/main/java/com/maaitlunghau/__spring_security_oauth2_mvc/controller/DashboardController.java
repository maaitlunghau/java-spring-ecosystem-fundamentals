package com.maaitlunghau.__spring_security_oauth2_mvc.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.maaitlunghau.__spring_security_oauth2_mvc.model.User;
import com.maaitlunghau.__spring_security_oauth2_mvc.security.UserAware;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal instanceof UserAware userAware) {
            User user = userAware.getUser();
            model.addAttribute("user", user);
        }
        return "dashboard";
    }
}
