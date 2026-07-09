package com.maaitlunghau.myFirstApp;

import org.springframework.stereotype.Component;

@Component
public class Macbook implements Computer {
    
    public void compile() {
        System.out.println("Compiling with 404 bugs by Macbook.");
    }
}   
