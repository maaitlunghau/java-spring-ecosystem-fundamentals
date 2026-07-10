package com.maaitlunghau.__rest_api_jpa_mysql.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // @RequestMapping("/")
    // @RequestMapping(value = "/", method = RequestMethod.GET)

    @GetMapping("/")
    public String home() {
        return "04-rest-api-jpa-mysql";
    }
}
