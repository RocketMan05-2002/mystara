package com.mystara.buyer.controller;

import com.mystara.buyer.model.Theme;
import com.mystara.buyer.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<List<Theme>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theme> getThemeById(@PathVariable String id) {
        return themeService.getThemeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Theme>> getThemesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(themeService.getThemesByCategory(category));
    }
}

