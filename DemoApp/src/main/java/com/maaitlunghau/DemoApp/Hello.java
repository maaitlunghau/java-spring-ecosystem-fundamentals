package com.maaitlunghau.DemoApp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author maaitlunghau
 * @version 1.0
 * @since 2024-06-10
 * 
 * ------------------------------------------
 * @Controller vs @RestController 
 * ------------------------------------------
 * - @Controller: 
 * + annotation gốc - đánh dấu class là một web controller
 * + mặc định các method sẽ trả về tên của một View (HTML Template: Thymeleaf, JSP, ...), không phải data trực tiếp. 
 * 
 * - @RestController:
 * + = @Controller + @ResponseBody
 * + @ResponseBody: trả về dữ liệu trực tiếp thông qua HTTP response body (JSON, XML, plain text, ...), không phải tên của một View.
 * 
 * 
 * ------------------------------------------
 * @RequestMapping
 * ------------------------------------------
 * - Map URL vào method
 * - Nói với Spring rằng: Khi có request đến URL này, hãy gọi method này. 
 * - Có thể map nhiều URL vào cùng một method.
 * - Các annotation chuyên biệt hơn (dùng thay thế):
 * + @GetMapping("/path")
 * + @PostMapping("/path")
 * + @PutMapping("/path")
 * + @DeleteMapping("/path")
 * 
*/

@RestController
public class Hello {

    @RequestMapping("/")
    public String hello() {
        return "Hello maaitlunghau";
    }
}
