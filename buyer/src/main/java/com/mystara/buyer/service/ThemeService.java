package com.mystara.buyer.service;

import com.mystara.buyer.model.Theme;
import com.mystara.buyer.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;

    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    public Optional<Theme> getThemeById(String id) {
        return themeRepository.findById(id);
    }

    public List<Theme> getThemesByCategory(String category) {
        return themeRepository.findByCategory(category);
    }
}

