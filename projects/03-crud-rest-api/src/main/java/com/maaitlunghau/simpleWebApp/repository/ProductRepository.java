package com.maaitlunghau.simpleWebApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maaitlunghau.simpleWebApp.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
