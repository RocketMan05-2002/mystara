package com.mystara.buyer.repository;

import com.mystara.buyer.model.Theme;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends MongoRepository<Theme, String> {
    Optional<Theme> findByName(String name);
    List<Theme> findByCategory(String category);
}

