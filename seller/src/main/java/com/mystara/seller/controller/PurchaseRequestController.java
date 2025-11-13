package com.mystara.seller.controller;

import com.mystara.seller.model.PurchaseRequest;
import com.mystara.seller.service.PurchaseRequestService;
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

    @GetMapping
    public ResponseEntity<List<PurchaseRequest>> getRequestsBySeller(
            @RequestHeader("X-Seller-Id") String sellerId) {
        return ResponseEntity.ok(requestService.getRequestsBySeller(sellerId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PurchaseRequest>> getPendingRequests(
            @RequestHeader("X-Seller-Id") String sellerId) {
        return ResponseEntity.ok(requestService.getPendingRequestsBySeller(sellerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRequest> getRequestById(@PathVariable String id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PurchaseRequest> approveRequest(
            @PathVariable String id,
            @RequestHeader("X-Seller-Id") String sellerId) {
        try {
            PurchaseRequest request = requestService.approveRequest(id, sellerId);
            return ResponseEntity.ok(request);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PurchaseRequest> rejectRequest(
            @PathVariable String id,
            @RequestHeader("X-Seller-Id") String sellerId,
            @RequestBody(required = false) RejectRequest rejectRequest) {
        try {
            String reason = rejectRequest != null ? rejectRequest.getReason() : null;
            PurchaseRequest request = requestService.rejectRequest(id, sellerId, reason);
            return ResponseEntity.ok(request);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Data
    static class RejectRequest {
        private String reason;
    }
}

