package com.maaitlunghau.simpleWebApp.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maaitlunghau.simpleWebApp.model.Product;

@Service
public class ProductService {

    List<Product> products = Arrays.asList(
        new Product(1, "Macbook Pro", 2500),
        new Product(2, "iPhone 15", 999),
        new Product(3, "iPad Air", 749)
    );

    public List<Product> getProducts() {
        return products;
    }
}
