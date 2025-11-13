package com.mystara.buyer.repository;

import com.mystara.buyer.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByThemeId(String themeId);
    List<Product> findByStatus(String status);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByThemeIdAndStatus(String themeId, String status);
}

