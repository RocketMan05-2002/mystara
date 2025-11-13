package com.mystara.buyer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String themeId;
    private String sellerId;
}

