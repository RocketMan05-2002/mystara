package com.mystara.buyer.repository;

import com.mystara.buyer.model.PurchaseRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends MongoRepository<PurchaseRequest, String> {
    List<PurchaseRequest> findByBuyerId(String buyerId);
    List<PurchaseRequest> findBySellerId(String sellerId);
    List<PurchaseRequest> findByBuyerIdAndStatus(String buyerId, String status);
    List<PurchaseRequest> findBySellerIdAndStatus(String sellerId, String status);
    Optional<PurchaseRequest> findByIdAndBuyerId(String id, String buyerId);
    Optional<PurchaseRequest> findByIdAndSellerId(String id, String sellerId);
}

