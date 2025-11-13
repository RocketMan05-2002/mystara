package com.mystara.seller.service;

import com.mystara.seller.model.Seller;
import com.mystara.seller.repository.SellerRepository;
import com.mystara.seller.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> register(String email, String password, String name, String phone, String businessName, String address) {
        if (sellerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Seller seller = new Seller();
        seller.setEmail(email);
        seller.setPassword(passwordEncoder.encode(password));
        seller.setName(name);
        seller.setPhone(phone);
        seller.setBusinessName(businessName);
        seller.setAddress(address);
        seller.setCreatedAt(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());

        Seller savedSeller = sellerRepository.save(seller);

        String token = jwtUtil.generateToken(savedSeller.getEmail(), savedSeller.getId(), "SELLER");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("seller", Map.of(
                "id", savedSeller.getId(),
                "email", savedSeller.getEmail(),
                "name", savedSeller.getName(),
                "businessName", savedSeller.getBusinessName()
        ));
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Optional<Seller> sellerOpt = sellerRepository.findByEmail(email);
        if (sellerOpt.isEmpty() || !passwordEncoder.matches(password, sellerOpt.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        Seller seller = sellerOpt.get();
        String token = jwtUtil.generateToken(seller.getEmail(), seller.getId(), "SELLER");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("seller", Map.of(
                "id", seller.getId(),
                "email", seller.getEmail(),
                "name", seller.getName(),
                "businessName", seller.getBusinessName()
        ));
        return response;
    }

    public Optional<Seller> getSellerById(String id) {
        return sellerRepository.findById(id);
    }
}

