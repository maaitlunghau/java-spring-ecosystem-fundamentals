package com.maaitlunghau.myFirstApp;

import org.springframework.stereotype.Component;

@Component
public class Macbook implements Computer {
    
    /**
     * Prints the Macbook compilation message.
     */
    public void compile() {
        System.out.println("Compiling with 404 bugs by Macbook.");
    }
}   
