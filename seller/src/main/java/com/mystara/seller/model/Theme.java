package com.mystara.seller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "themes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theme {
    @Id
    private String id;
    private String name;
    private String description;
    private String category; // e.g., "Gaming", "Nature", "Abstract", "Vintage"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

