package com.financetracker.repository;

import com.financetracker.entity.Category;
import com.financetracker.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByType(CategoryType type);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
