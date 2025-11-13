package com.mystara.payment.repository;

import com.mystara.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByBuyerId(String buyerId);
    List<Payment> findByBuyerIdAndStatus(String buyerId, String status);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}

