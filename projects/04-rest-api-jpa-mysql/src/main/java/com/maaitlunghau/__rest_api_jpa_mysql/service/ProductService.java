package com.maaitlunghau.__rest_api_jpa_mysql.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maaitlunghau.__rest_api_jpa_mysql.exception.ResourceNotFoundException;
import com.maaitlunghau.__rest_api_jpa_mysql.model.Product;
import com.maaitlunghau.__rest_api_jpa_mysql.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getProductId())) {
            throw new ResourceNotFoundException("Product", product.getProductId());
        }
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
