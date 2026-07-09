package com.maaitlunghau.simpleWebApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.maaitlunghau.simpleWebApp.model.Product;
import com.maaitlunghau.simpleWebApp.service.ProductService;

@RestController
public class ProductController {

    // Field Injection
    @Autowired
    ProductService productService;

    // @RequestMapping(value = "/products", method = RequestMethod.GET): cách cũ
    // @RequestMapping("/products") // catch all method for this URL
    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/products/{productId}")
    public Product getProductById(@PathVariable int productId) {
        return productService.getProductById(productId);
    }   

    @PostMapping("/products")
    public String addProduct(@RequestBody Product pro) { 
        // @RequestBody:  
        // nói với Spring: "Hãy đọc JSON từ body của HTTP request và convert thành object Java."
        productService.addProduct(pro);
        return "Created successfully";
    }

    @PutMapping("/products")
    public String updateProduct(@RequestBody Product pro) {
        productService.updateProduct(pro);
        return "Updated successfully";
    }

    @DeleteMapping("/products/{productId}") 
    public void deleteProduct(@PathVariable int productId) {
        productService.deleteProduct(productId);
    }
}   
