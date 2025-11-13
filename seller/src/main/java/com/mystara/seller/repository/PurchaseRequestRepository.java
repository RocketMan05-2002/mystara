package com.mystara.seller.repository;

import com.mystara.seller.model.PurchaseRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends MongoRepository<PurchaseRequest, String> {
    List<PurchaseRequest> findBySellerId(String sellerId);
    List<PurchaseRequest> findBySellerIdAndStatus(String sellerId, String status);
    Optional<PurchaseRequest> findByIdAndSellerId(String id, String sellerId);
}

