package com.maaitlunghau.simpleWebApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maaitlunghau.simpleWebApp.model.Product;
import com.maaitlunghau.simpleWebApp.service.ProductService;

@RestController
public class ProductController {

    // Field Injection
    @Autowired
    ProductService productService;

    @RequestMapping("/products")
    public List<Product> getProducts() {
        return productService.getProducts();
    }
}   
