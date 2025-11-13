package com.mystara.buyer.controller;

import com.mystara.buyer.model.Cart;
import com.mystara.buyer.service.CartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader("X-Buyer-Id") String buyerId) {
        try {
            return ResponseEntity.ok(cartService.getCart(buyerId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody AddItemRequest request) {
        try {
            Cart cart = cartService.addItemToCart(buyerId, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @PathVariable String productId,
            @RequestBody UpdateQuantityRequest request) {
        try {
            Cart cart = cartService.updateCartItemQuantity(buyerId, productId, request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @PathVariable String productId) {
        try {
            Cart cart = cartService.removeItemFromCart(buyerId, productId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-Buyer-Id") String buyerId) {
        try {
            cartService.clearCart(buyerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Data
    static class AddItemRequest {
        private String productId;
        private Integer quantity;
    }

    @Data
    static class UpdateQuantityRequest {
        private Integer quantity;
    }
}

