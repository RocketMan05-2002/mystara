package com.mystara.buyer.service;

import com.mystara.buyer.model.Cart;
import com.mystara.buyer.model.Order;
import com.mystara.buyer.model.OrderItem;
import com.mystara.buyer.model.Product;
import com.mystara.buyer.repository.CartRepository;
import com.mystara.buyer.repository.OrderRepository;
import com.mystara.buyer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public List<Order> getOrdersByBuyer(String buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    public List<Order> getOrdersByBuyerAndStatus(String buyerId, String status) {
        return orderRepository.findByBuyerIdAndStatus(buyerId, status);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrderFromCart(String buyerId, String shippingAddress) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for buyer: " + buyerId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create order.");
        }

        // Validate stock and create order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = productRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

                    if (!"ACTIVE".equals(product.getStatus())) {
                        throw new RuntimeException("Product " + product.getName() + " is no longer available");
                    }

                    if (product.getStock() < cartItem.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName() + 
                                ". Available: " + product.getStock() + ", Requested: " + cartItem.getQuantity());
                    }

                    // Update product stock
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    if (product.getStock() == 0) {
                        product.setStatus("OUT_OF_STOCK");
                    }
                    product.setUpdatedAt(LocalDateTime.now());
                    productRepository.save(product);

                    // Create order item
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setProductName(cartItem.getProductName());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setThemeId(cartItem.getThemeId());
                    orderItem.setSellerId(cartItem.getSellerId());
                    orderItem.setSubtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Create order
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setStatus("CONFIRMED");
        order.setShippingAddress(shippingAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return savedOrder;
    }

    public Order updateOrderStatus(String orderId, String status) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public void cancelOrder(String orderId, String buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("Order does not belong to buyer: " + buyerId);
        }

        if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        // Restore product stock
        order.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                if ("OUT_OF_STOCK".equals(product.getStatus()) && product.getStock() > 0) {
                    product.setStatus("ACTIVE");
                }
                product.setUpdatedAt(LocalDateTime.now());
                productRepository.save(product);
            }
        });

        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}

