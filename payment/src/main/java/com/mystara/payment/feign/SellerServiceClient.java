package com.mystara.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "seller", path = "/api/products")
public interface SellerServiceClient {
    @PostMapping("/{productId}/reduce-stock")
    Object reduceStock(@PathVariable String productId, @RequestBody Integer quantity);
}

