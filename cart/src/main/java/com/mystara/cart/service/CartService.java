package com.mystara.cart.service;

import com.mystara.cart.model.Cart;
import com.mystara.cart.model.CartItem;
import com.mystara.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public Cart getOrCreateCart(String buyerId) {
        return cartRepository.findByBuyerId(buyerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setBuyerId(buyerId);
                    newCart.setItems(new java.util.ArrayList<>());
                    newCart.setTotalAmount(java.math.BigDecimal.ZERO);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }


    public Cart updateItemQuantity(String buyerId, String productId, Integer quantity) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(quantity),
                        () -> { throw new RuntimeException("Item not found in cart"); });

        return cartRepository.save(cart);
    }

    public Cart addApprovedRequestToCart(String buyerId, String requestId, String productId, 
                                         String productName, java.math.BigDecimal price, 
                                         Integer quantity, String themeId, String sellerId) {
        Cart cart = getOrCreateCart(buyerId);

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getRequestId() != null && item.getRequestId().equals(requestId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Item already added from this request
            return cart;
        }

        CartItem newItem = new CartItem();
        newItem.setProductId(productId);
        newItem.setProductName(productName);
        newItem.setPrice(price);
        newItem.setQuantity(quantity);
        newItem.setThemeId(themeId);
        newItem.setSellerId(sellerId);
        newItem.setRequestId(requestId);
        cart.getItems().add(newItem);

        cart.setTotalAmount(cart.calculateTotal());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart getCart(String buyerId) {
        return cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));
    }

    public Cart removeItemFromCart(String buyerId, String productId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.setTotalAmount(cart.calculateTotal());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public void clearCart(String buyerId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));
        cart.getItems().clear();
        cart.setTotalAmount(java.math.BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}

