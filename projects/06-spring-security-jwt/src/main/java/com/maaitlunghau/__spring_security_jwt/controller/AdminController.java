package com.maaitlunghau.__spring_security_jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
        return "Welcome to the Admin Dashboard";
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello dev from secure URL";
    }
}
