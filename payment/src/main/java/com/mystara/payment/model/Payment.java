package com.mystara.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private String id;
    private String buyerId;
    private String cartId;
    private List<PaymentItem> items;
    private BigDecimal amount;
    private String currency; // USD, EUR, etc.
    private String status; // PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

