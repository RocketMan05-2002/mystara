package com.mystara.seller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "sellers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String name;
    private String phone;
    private String businessName;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

