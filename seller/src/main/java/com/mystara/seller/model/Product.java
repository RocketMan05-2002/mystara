package com.mystara.seller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    private String sellerId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String themeId; // Reference to theme
    private List<String> images; // URLs or paths to product images
    private String status; // ACTIVE, INACTIVE, OUT_OF_STOCK
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

