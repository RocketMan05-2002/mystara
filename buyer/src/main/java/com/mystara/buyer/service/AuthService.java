package com.mystara.buyer.service;

import com.mystara.buyer.model.Buyer;
import com.mystara.buyer.repository.BuyerRepository;
import com.mystara.buyer.util.JwtUtil;
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
    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> register(String email, String password, String name, String phone, String address) {
        if (buyerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Buyer buyer = new Buyer();
        buyer.setEmail(email);
        buyer.setPassword(passwordEncoder.encode(password));
        buyer.setName(name);
        buyer.setPhone(phone);
        buyer.setAddress(address);
        buyer.setCreatedAt(LocalDateTime.now());
        buyer.setUpdatedAt(LocalDateTime.now());

        Buyer savedBuyer = buyerRepository.save(buyer);

        String token = jwtUtil.generateToken(savedBuyer.getEmail(), savedBuyer.getId(), "BUYER");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("buyer", Map.of(
                "id", savedBuyer.getId(),
                "email", savedBuyer.getEmail(),
                "name", savedBuyer.getName()
        ));
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Optional<Buyer> buyerOpt = buyerRepository.findByEmail(email);
        if (buyerOpt.isEmpty() || !passwordEncoder.matches(password, buyerOpt.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        Buyer buyer = buyerOpt.get();
        String token = jwtUtil.generateToken(buyer.getEmail(), buyer.getId(), "BUYER");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("buyer", Map.of(
                "id", buyer.getId(),
                "email", buyer.getEmail(),
                "name", buyer.getName()
        ));
        return response;
    }

    public Optional<Buyer> getBuyerById(String id) {
        return buyerRepository.findById(id);
    }
}

