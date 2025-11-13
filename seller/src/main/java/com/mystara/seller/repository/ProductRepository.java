package com.mystara.seller.repository;

import com.mystara.seller.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findBySellerId(String sellerId);
    List<Product> findByThemeId(String themeId);
    List<Product> findBySellerIdAndThemeId(String sellerId, String themeId);
    List<Product> findByStatus(String status);
    Optional<Product> findByIdAndSellerId(String id, String sellerId);
}

