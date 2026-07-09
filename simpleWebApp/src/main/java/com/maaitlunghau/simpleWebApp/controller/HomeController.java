package com.maaitlunghau.simpleWebApp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/")
    @ResponseBody // maybe don't need this
    public String greet() {
        return "Welcome to my simple web application!";
    }

    @RequestMapping("/about")
    public String about() {
        return "This is a simple web application built with Spring Boot.";
    }
}
