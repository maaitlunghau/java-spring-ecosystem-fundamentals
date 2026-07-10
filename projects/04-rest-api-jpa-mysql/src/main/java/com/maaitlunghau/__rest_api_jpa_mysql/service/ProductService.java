package com.maaitlunghau.__rest_api_jpa_mysql.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maaitlunghau.__rest_api_jpa_mysql.dto.CreateProductRequest;
import com.maaitlunghau.__rest_api_jpa_mysql.dto.ProductResponse;
import com.maaitlunghau.__rest_api_jpa_mysql.dto.UpdateProductRequest;
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

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    // Trả ProductResponse thay vì Optional — nếu không tìm thấy thì throw exception,
    // GlobalExceptionHandler sẽ bắt và trả 404. Controller không cần xử lý Optional.
    // (Returns DTO directly: throws 404 exception instead of Optional.empty)
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Map từ DTO sang Entity — Entity không biết gì về DTO
        // (Map DTO → Entity: entity stays ignorant of API layer)
        Product product = new Product(
                request.productName(),
                request.productPrice(),
                request.quantity()
        );
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        // Cập nhật trực tiếp trên managed entity — Hibernate dirty checking tự detect
        // thay đổi và flush vào DB khi transaction commit, không cần gọi save() tường minh.
        // (Update managed entity fields: Hibernate dirty checking flushes changes on commit,
        // no explicit save() needed)
        product.setProductName(request.productName());
        product.setProductPrice(request.productPrice());
        product.setQuantity(request.quantity());

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
    }
}
