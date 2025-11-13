package com.mystara.buyer.service;

import com.mystara.buyer.model.Cart;
import com.mystara.buyer.model.CartItem;
import com.mystara.buyer.model.Product;
import com.mystara.buyer.repository.CartRepository;
import com.mystara.buyer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

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

    public Cart addItemToCart(String buyerId, String productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new RuntimeException("Product is not available for purchase");
        }

        if (product.getStock() == null || product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        Cart cart = getOrCreateCart(buyerId);

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
            }
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(quantity);
            newItem.setThemeId(product.getThemeId());
            newItem.setSellerId(product.getSellerId());
            cart.getItems().add(newItem);
        }

        cart.setTotalAmount(cart.calculateTotal());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(String buyerId, String productId, Integer quantity) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (quantity <= 0) {
            return removeItemFromCart(buyerId, productId);
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        cart.setTotalAmount(cart.calculateTotal());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(String buyerId, String productId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.setTotalAmount(cart.calculateTotal());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart getCart(String buyerId) {
        return cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));
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
