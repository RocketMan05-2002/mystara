package com.mystara.cart.repository;

import com.mystara.cart.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByBuyerId(String buyerId);
   // Cart updateItemQuantity(String buyerId, String productId, Integer quantity);

}

