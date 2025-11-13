package com.mystara.payment.controller;

import com.mystara.payment.model.Payment;
import com.mystara.payment.model.PaymentItem;
import com.mystara.payment.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody CreatePaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(
                    buyerId,
                    request.getCartId(),
                    request.getItems(),
                    request.getAmount(),
                    request.getCurrency()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Payment> confirmPayment(
            @PathVariable String id,
            @RequestBody ConfirmPaymentRequest request) {
        try {
            Payment payment = paymentService.confirmPayment(id, request.getRazorpayPaymentId());
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getPaymentsByBuyer(
            @RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBuyer(buyerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable String id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    static class CreatePaymentRequest {
        private String cartId;
        private List<PaymentItem> items;
        private BigDecimal amount;
        private String currency;
    }

    @Data
    static class ConfirmPaymentRequest {

        private String razorpayPaymentId;
        private String razorpayOrderId;
        private String razorpaySignature;
    }
}

