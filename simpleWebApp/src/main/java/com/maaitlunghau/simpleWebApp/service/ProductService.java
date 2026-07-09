package com.maaitlunghau.simpleWebApp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maaitlunghau.simpleWebApp.model.Product;

@Service
public class ProductService {

    List<Product> products = new ArrayList<>(Arrays.asList(
        new Product(1, "Macbook Pro", 2500),
        new Product(2, "iPhone 15", 999),
        new Product(3, "iPad Air", 749)
    ));

    public List<Product> getProducts() {
        return products;
    }

    public Product getProductById(int productId) {
        return products.stream()
            .filter(p -> p.getProductId() == productId)
            .findFirst().orElse(new Product(0, "No Item", 0));
    }

    public void addProduct(Product pro) {
        products.add(pro);
    }
}
