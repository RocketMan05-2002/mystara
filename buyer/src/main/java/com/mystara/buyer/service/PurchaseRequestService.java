package com.mystara.buyer.service;

import com.mystara.buyer.model.Product;
import com.mystara.buyer.model.PurchaseRequest;
import com.mystara.buyer.repository.ProductRepository;
import com.mystara.buyer.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {
    private final PurchaseRequestRepository requestRepository;
    private final ProductRepository productRepository;

    public PurchaseRequest createRequest(String buyerId, String productId, Integer quantity, String message) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new RuntimeException("Product is not available for purchase");
        }

        if (product.getStock() == null || product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        PurchaseRequest request = new PurchaseRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(product.getSellerId());
        request.setProductId(productId);
        request.setProductName(product.getName());
        request.setQuantity(quantity);
        request.setPrice(product.getPrice());
        request.setThemeId(product.getThemeId());
        request.setStatus("PENDING");
        request.setMessage(message);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public List<PurchaseRequest> getRequestsByBuyer(String buyerId) {
        return requestRepository.findByBuyerId(buyerId);
    }

    public List<PurchaseRequest> getPendingRequestsByBuyer(String buyerId) {
        return requestRepository.findByBuyerIdAndStatus(buyerId, "PENDING");
    }

    public List<PurchaseRequest> getApprovedRequestsByBuyer(String buyerId) {
        return requestRepository.findByBuyerIdAndStatus(buyerId, "APPROVED");
    }

    public Optional<PurchaseRequest> getRequestById(String id) {
        return requestRepository.findById(id);
    }

    public void cancelRequest(String id, String buyerId) {
        PurchaseRequest request = requestRepository.findByIdAndBuyerId(id, buyerId)
                .orElseThrow(() -> new RuntimeException("Request not found or not authorized"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        request.setStatus("CANCELLED");
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }
}

