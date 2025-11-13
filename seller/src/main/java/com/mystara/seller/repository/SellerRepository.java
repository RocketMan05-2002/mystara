package com.mystara.seller.repository;

import com.mystara.seller.model.Seller;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends MongoRepository<Seller, String> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
}

