package com.mystara.seller.controller;

import com.mystara.seller.model.Theme;
import com.mystara.seller.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<?> getAllThemes() {
        try {
            List<Theme> themes = themeService.getAllThemes();
            return ResponseEntity.ok(themes != null ? themes : java.util.Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch themes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theme> getThemeById(@PathVariable String id) {
        return themeService.getThemeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Theme> getThemeByName(@PathVariable String name) {
        return themeService.getThemeByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Theme>> getThemesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(themeService.getThemesByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Theme> createTheme(@RequestBody Theme theme) {
        try {
            Theme createdTheme = themeService.createTheme(theme);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTheme);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theme> updateTheme(@PathVariable String id, @RequestBody Theme theme) {
        try {
            Theme updatedTheme = themeService.updateTheme(id, theme);
            return ResponseEntity.ok(updatedTheme);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable String id) {
        try {
            themeService.deleteTheme(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

