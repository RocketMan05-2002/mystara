package com.mystara.seller.service;

import com.mystara.seller.model.Theme;
import com.mystara.seller.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;

    public List<Theme> getAllThemes() {
        try {
            List<Theme> themes = themeRepository.findAll();
            return themes != null ? themes : java.util.Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch themes: " + e.getMessage(), e);
        }
    }

    public Optional<Theme> getThemeById(String id) {
        return themeRepository.findById(id);
    }

    public Optional<Theme> getThemeByName(String name) {
        return themeRepository.findByName(name);
    }

    public List<Theme> getThemesByCategory(String category) {
        return themeRepository.findByCategory(category);
    }

    public Theme createTheme(Theme theme) {
        theme.setCreatedAt(LocalDateTime.now());
        theme.setUpdatedAt(LocalDateTime.now());
        return themeRepository.save(theme);
    }

    public Theme updateTheme(String id, Theme themeDetails) {
        return themeRepository.findById(id)
                .map(theme -> {
                    theme.setName(themeDetails.getName());
                    theme.setDescription(themeDetails.getDescription());
                    theme.setCategory(themeDetails.getCategory());
                    theme.setUpdatedAt(LocalDateTime.now());
                    return themeRepository.save(theme);
                })
                .orElseThrow(() -> new RuntimeException("Theme not found with id: " + id));
    }

    public void deleteTheme(String id) {
        themeRepository.deleteById(id);
    }
}

