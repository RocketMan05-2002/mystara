package com.mystara.buyer.controller;

import com.mystara.buyer.model.Order;
import com.mystara.buyer.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByBuyer(@RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(orderService.getOrdersByBuyer(buyerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByBuyerAndStatus(buyerId, status));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrderFromCart(buyerId, request.getShippingAddress());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request) {
        try {
            Order order = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable String id,
            @RequestHeader("X-Buyer-Id") String buyerId) {
        try {
            orderService.cancelOrder(id, buyerId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Data
    static class CreateOrderRequest {
        private String shippingAddress;
    }

    @Data
    static class UpdateStatusRequest {
        private String status;
    }
}

