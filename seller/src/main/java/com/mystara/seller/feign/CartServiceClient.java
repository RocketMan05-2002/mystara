package com.mystara.seller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "cart-service", path = "/api/cart")
public interface CartServiceClient {
    @PostMapping("/approved-request")
    Object addApprovedRequestToCart(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody Map<String, Object> request
    );
}

