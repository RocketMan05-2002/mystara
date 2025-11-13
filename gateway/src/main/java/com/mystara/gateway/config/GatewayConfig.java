package com.mystara.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Seller Service Routes
                // Products, Themes, Purchase Requests (Seller side)
                .route("seller-service", r -> r
                        .path("/api/products/**", "/api/themes/**", "/api/requests/**")
                        .uri("lb://seller"))

                // Buyer Service Routes
                // Products (browsing), Themes (browsing), Purchase Requests (buyer side)
                .route("buyer-service", r -> r
                        .path("/api/buyer/**")
                        .filters(f -> f.rewritePath("/api/buyer/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://buyer"))

                // Cart Service Routes
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .uri("lb://cart-service"))

                // Payment Service Routes
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .uri("lb://payment-service"))

                // Auth Routes - Buyer
                .route("buyer-auth", r -> r
                        .path("/api/buyer/auth/**")
                        .filters(f -> f.rewritePath("/api/buyer/auth/(?<segment>.*)", "/api/auth/${segment}"))
                        .uri("lb://buyer"))

                // Auth Routes - Seller
                .route("seller-auth", r -> r
                        .path("/api/seller/auth/**")
                        .filters(f -> f.rewritePath("/api/seller/auth/(?<segment>.*)", "/api/auth/${segment}"))
                        .uri("lb://seller"))

                .build();
    }
}

