package com.mystara.seller.service;

import com.mystara.seller.model.Product;
import com.mystara.seller.repository.ProductRepository;
import com.mystara.seller.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ThemeRepository themeRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<Product> getProductsByTheme(String themeId) {
        return productRepository.findByThemeId(themeId);
    }

    public List<Product> getProductsBySellerAndTheme(String sellerId, String themeId) {
        return productRepository.findBySellerIdAndThemeId(sellerId, themeId);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductByIdAndSeller(String id, String sellerId) {
        return productRepository.findByIdAndSellerId(id, sellerId);
    }

    public Product createProduct(Product product) {
        // Validate theme exists
        if (product.getThemeId() != null && !themeRepository.existsById(product.getThemeId())) {
            throw new RuntimeException("Theme not found with id: " + product.getThemeId());
        }

        if (product.getStatus() == null || product.getStatus().isEmpty()) {
            product.setStatus("ACTIVE");
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public Product updateProduct(String id, String sellerId, Product productDetails) {
        return productRepository.findByIdAndSellerId(id, sellerId)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    
                    // Validate theme if being updated
                    if (productDetails.getThemeId() != null && !productDetails.getThemeId().equals(product.getThemeId())) {
                        if (!themeRepository.existsById(productDetails.getThemeId())) {
                            throw new RuntimeException("Theme not found with id: " + productDetails.getThemeId());
                        }
                        product.setThemeId(productDetails.getThemeId());
                    }
                    
                    if (productDetails.getImages() != null) {
                        product.setImages(productDetails.getImages());
                    }
                    if (productDetails.getStatus() != null) {
                        product.setStatus(productDetails.getStatus());
                    }
                    product.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id + " for seller: " + sellerId));
    }

    public void deleteProduct(String id, String sellerId) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id + " for seller: " + sellerId));
        productRepository.delete(product);
    }

    public Product updateStock(String id, String sellerId, Integer stock) {
        return productRepository.findByIdAndSellerId(id, sellerId)
                .map(product -> {
                    product.setStock(stock);
                    if (stock <= 0) {
                        product.setStatus("OUT_OF_STOCK");
                    } else if ("OUT_OF_STOCK".equals(product.getStatus())) {
                        product.setStatus("ACTIVE");
                    }
                    product.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id + " for seller: " + sellerId));
    }

    public Product reduceStock(String productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (product.getStock() == null || product.getStock() < quantity) {
                        throw new RuntimeException("Insufficient stock. Available: " + product.getStock() + ", Requested: " + quantity);
                    }
                    int newStock = product.getStock() - quantity;
                    product.setStock(newStock);
                    if (newStock <= 0) {
                        product.setStatus("OUT_OF_STOCK");
                    }
                    product.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }
}

