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

    /**
     * Returns the current product list.
     *
     * @return the current list of products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Finds a product by its identifier.
     *
     * @param  productId  the product identifier to search for
     * @return            the matching product, or a product with ID {@code 0} and name {@code "No Item"} when no match is found
     */
    public Product getProductById(int productId) {
        return products.stream()
            .filter(p -> p.getProductId() == productId)
            .findFirst().orElse(new Product(0, "No Item", 0));
    }

    /**
     * Adds a product to the list.
     *
     * @param pro the product to add
     */
    public void addProduct(Product pro) {
        products.add(pro);
    }

    /**
     * Replaces the product with the same identifier as the supplied product.
     *
     * @param pro the product to store
     */
    public void updateProduct(Product pro) {
        int index = 0;

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == pro.getProductId()) {
                index = i;
                break;
            }
        }

        products.set(index, pro);
    }

    /**
     * Removes all products with the specified ID.
     *
     * @param productId the ID of the product to remove
     */
    public void deleteProduct(int productId) {
        products.removeIf(p -> p.getProductId() == productId);
    }
}
