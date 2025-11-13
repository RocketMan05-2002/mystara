package com.mystara.buyer.controller;

import com.mystara.buyer.model.PurchaseRequest;
import com.mystara.buyer.service.PurchaseRequestService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class PurchaseRequestController {
    private final PurchaseRequestService requestService;

    @PostMapping
    public ResponseEntity<PurchaseRequest> createRequest(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody CreateRequestRequest request) {
        try {
            PurchaseRequest purchaseRequest = requestService.createRequest(
                    buyerId,
                    request.getProductId(),
                    request.getQuantity(),
                    request.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(purchaseRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PurchaseRequest>> getRequestsByBuyer(
            @RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(requestService.getRequestsByBuyer(buyerId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PurchaseRequest>> getPendingRequests(
            @RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(requestService.getPendingRequestsByBuyer(buyerId));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<PurchaseRequest>> getApprovedRequests(
            @RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(requestService.getApprovedRequestsByBuyer(buyerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRequest> getRequestById(@PathVariable String id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable String id,
            @RequestHeader("X-Buyer-Id") String buyerId) {
        try {
            requestService.cancelRequest(id, buyerId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Data
    static class CreateRequestRequest {
        private String productId;
        private Integer quantity;
        private String message;
    }
}

