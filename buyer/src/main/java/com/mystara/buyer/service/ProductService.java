package com.mystara.buyer.service;

import com.mystara.buyer.model.Product;
import com.mystara.buyer.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllAvailableProducts() {
        return productRepository.findByStatus("ACTIVE");
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByTheme(String themeId) {
        return productRepository.findByThemeIdAndStatus(themeId, "ACTIVE");
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .filter(product -> "ACTIVE".equals(product.getStatus()))
                .toList();
    }

    public boolean isProductAvailable(String productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> "ACTIVE".equals(product.getStatus()) 
                        && product.getStock() != null 
                        && product.getStock() >= quantity)
                .orElse(false);
    }
}

