package com.mystara.buyer.repository;

import com.mystara.buyer.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByBuyerId(String buyerId);
    List<Order> findByBuyerIdAndStatus(String buyerId, String status);
}

