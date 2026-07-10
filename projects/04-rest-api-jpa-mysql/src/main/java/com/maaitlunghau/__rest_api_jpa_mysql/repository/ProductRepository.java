package com.maaitlunghau.__rest_api_jpa_mysql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maaitlunghau.__rest_api_jpa_mysql.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
