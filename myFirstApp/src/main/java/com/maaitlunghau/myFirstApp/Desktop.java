package com.maaitlunghau.myFirstApp;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * ------------------------------------
 * @Primary
 * ------------------------------------
 * - Đánh dấu Bean này ưu tiên sử dụng khi có Bean khác cùng implement Computer
 * - Tuy nhiên, sự uy tiên của @Primary sẽ thấp hơn (ko bằng) so với @Qualifier
 */
@Component
@Primary 
public class Desktop implements Computer {
    
    /**
     * Prints a desktop compilation message.
     */
    public void compile() {
        System.out.println("Compiling with 404 bugs by Desktop.");
    }
}   
