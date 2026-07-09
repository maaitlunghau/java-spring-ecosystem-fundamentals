package com.maaitlunghau.simpleWebApp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    /**
     * Handles requests for the login page.
     *
     * @return the login page response text
     */
    @RequestMapping("/login") 
    public String login() {
        return "Login page...";
    }
}   

