package com.maaitlunghau.myFirstApp;

import org.springframework.stereotype.Component;

/**
 * ----------------------------------
 * @Component
 * ----------------------------------
 * - nói với Spring: mày tự quản lý class Dev này đi nhé, tao sẽ không tự new nữa.
 * 
 * - cơ chế hoạt động: 
 * + khi SpringApplication.run(), Spring sẽ quét toàn bộ package
 * + tìm tất cả class có @Component, rồi tự động tạo object chúng và lưu vào một kho gọi là IoC Container.
 * 
 * - Object được Spring tạo và quản lý sẽ được gọi là Bean
 */

@Component 
public class Dev {

    public void build() {
        System.out.println("DEV: Building the application...");
    }
}
