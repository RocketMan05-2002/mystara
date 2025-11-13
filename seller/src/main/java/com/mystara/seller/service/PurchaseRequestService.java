package com.mystara.seller.service;

import com.mystara.seller.feign.CartServiceClient;
import com.mystara.seller.model.Product;
import com.mystara.seller.model.PurchaseRequest;
import com.mystara.seller.repository.ProductRepository;
import com.mystara.seller.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {
    private final PurchaseRequestRepository requestRepository;
    private final ProductRepository productRepository;
    private final CartServiceClient cartServiceClient;

    public List<PurchaseRequest> getRequestsBySeller(String sellerId) {
        return requestRepository.findBySellerId(sellerId);
    }

    public List<PurchaseRequest> getPendingRequestsBySeller(String sellerId) {
        return requestRepository.findBySellerIdAndStatus(sellerId, "PENDING");
    }

    public Optional<PurchaseRequest> getRequestById(String id) {
        return requestRepository.findById(id);
    }

    public PurchaseRequest approveRequest(String id, String sellerId) {
        PurchaseRequest request = requestRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new RuntimeException("Request not found or not authorized"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Only pending requests can be approved");
        }

        // Validate product stock
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Product does not belong to seller");
        }

        if (product.getStock() == null || product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        request.setStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());
        PurchaseRequest savedRequest = requestRepository.save(request);

        // Automatically add approved request to buyer's cart
        addApprovedRequestToCart(savedRequest);

        return savedRequest;
    }

    private void addApprovedRequestToCart(PurchaseRequest request) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("requestId", request.getId());
            body.put("productId", request.getProductId());
            body.put("productName", request.getProductName());
            body.put("price", request.getPrice());
            body.put("quantity", request.getQuantity());
            body.put("themeId", request.getThemeId());
            body.put("sellerId", request.getSellerId());

            cartServiceClient.addApprovedRequestToCart(request.getBuyerId(), body);
        } catch (Exception e) {
            System.err.println("Failed to add approved request to cart: " + e.getMessage());
            // Don't fail the approval if cart addition fails
        }
    }

    public PurchaseRequest rejectRequest(String id, String sellerId, String reason) {
        PurchaseRequest request = requestRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new RuntimeException("Request not found or not authorized"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Only pending requests can be rejected");
        }

        request.setStatus("REJECTED");
        if (reason != null) {
            request.setMessage(request.getMessage() + " [Rejected: " + reason + "]");
        }
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }
}

