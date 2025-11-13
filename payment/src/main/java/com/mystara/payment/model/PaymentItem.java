package com.mystara.payment.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String sellerId;


}

