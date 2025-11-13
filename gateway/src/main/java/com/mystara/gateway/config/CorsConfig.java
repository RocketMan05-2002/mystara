package com.mystara.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Allow all origins - use addAllowedOriginPattern instead of setAllowedOrigins with *
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);
        // Expose headers that frontend might need
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "X-User-Id", "X-User-Role", "X-Buyer-Id", "X-Seller-Id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        CorsWebFilter corsWebFilter = new CorsWebFilter(source);
        // Set high priority so CORS filter runs before JWT filter
        return corsWebFilter;
    }
}

