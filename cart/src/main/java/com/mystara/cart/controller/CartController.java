package com.mystara.cart.controller;

import com.mystara.cart.model.Cart;
import com.mystara.cart.service.CartService;
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

    @PostMapping("/approved-request")
    public ResponseEntity<Cart> addApprovedRequestToCart(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody AddApprovedRequestRequest request) {
        try {
            Cart cart = cartService.addApprovedRequestToCart(
                    buyerId,
                    request.getRequestId(),
                    request.getProductId(),
                    request.getProductName(),
                    request.getPrice(),
                    request.getQuantity(),
                    request.getThemeId(),
                    request.getSellerId()
            );
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
    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateItemQuantity(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @PathVariable String productId,
            @RequestParam("quantity") Integer quantity) {
        try {
            Cart cart = cartService.updateItemQuantity(buyerId, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
    static class AddApprovedRequestRequest {
        private String requestId;
        private String productId;
        private String productName;
        private java.math.BigDecimal price;
        private Integer quantity;
        private String themeId;
        private String sellerId;
    }
}

