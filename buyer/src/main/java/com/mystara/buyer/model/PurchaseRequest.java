package com.mystara.buyer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "purchase_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {
    @Id
    private String id;
    private String buyerId;
    private String sellerId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String themeId;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private String message; // Optional message from buyer
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

