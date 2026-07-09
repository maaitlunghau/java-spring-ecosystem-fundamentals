package com.maaitlunghau.simpleWebApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maaitlunghau.simpleWebApp.model.Product;
import com.maaitlunghau.simpleWebApp.repository.ProductRepository;

@Service
public class ProductService {

    // List<Product> products = new ArrayList<>(Arrays.asList(
    //     new Product(1, "Macbook Pro", 2500),
    //     new Product(2, "iPhone 15", 999),
    //     new Product(3, "iPad Air", 749)
    // ));

    @Autowired
    ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int productId) {
        return productRepository
            .findById(productId)
            .orElse(new Product(0, "No Item", 0));
    }

    public void addProduct(Product pro) {
        productRepository.save(pro);
    }

    public void updateProduct(Product pro) {
        productRepository.save(pro);
    }

    public void deleteProduct(int productId) {
        productRepository.deleteById(productId);
    }
}
