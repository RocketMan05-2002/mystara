package com.mystara.buyer.repository;

import com.mystara.buyer.model.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRepository extends MongoRepository<Buyer, String> {
    Optional<Buyer> findByEmail(String email);
    boolean existsByEmail(String email);
}

