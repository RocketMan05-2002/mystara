package com.mystara.seller.controller;

import com.mystara.seller.model.Product;
import com.mystara.seller.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String themeId,
            @RequestParam(required = false) String sellerId) {
        boolean hasTheme = StringUtils.hasText(themeId);
        boolean hasSeller = StringUtils.hasText(sellerId);

        if (hasTheme && hasSeller) {
            return ResponseEntity.ok(productService.getProductsBySellerAndTheme(sellerId, themeId));
        }

        if (hasTheme) {
            return ResponseEntity.ok(productService.getProductsByTheme(themeId));
        }

        if (hasSeller) {
            return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
        }

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> getProductsBySeller(@PathVariable String sellerId) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<Product>> getProductsByTheme(@PathVariable String themeId) {
        return ResponseEntity.ok(productService.getProductsByTheme(themeId));
    }

    @GetMapping("/seller/{sellerId}/theme/{themeId}")
    public ResponseEntity<List<Product>> getProductsBySellerAndTheme(
            @PathVariable String sellerId,
            @PathVariable String themeId) {
        return ResponseEntity.ok(productService.getProductsBySellerAndTheme(sellerId, themeId));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestHeader("X-Seller-Id") String sellerId,
            @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, sellerId, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable String id,
            @RequestHeader("X-Seller-Id") String sellerId,
            @RequestBody Integer stock) {
        try {
            Product updatedProduct = productService.updateStock(id, sellerId, stock);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<Product> reduceStock(
            @PathVariable String id,
            @RequestBody Integer quantity) {
        try {
            Product updatedProduct = productService.reduceStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @RequestHeader("X-Seller-Id") String sellerId) {
        try {
            productService.deleteProduct(id, sellerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

