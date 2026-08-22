package com.financetracker.service;

import com.financetracker.dto.CategoryRequest;
import com.financetracker.dto.CategoryResponse;
import com.financetracker.entity.Category;
import com.financetracker.entity.CategoryType;
import com.financetracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> responseList = new ArrayList<>();

        for (Category category : categories) {
            responseList.add(new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getType()
            ));
        }

        return responseList;
    }

    public List<CategoryResponse> getCategoriesByType(CategoryType type) {
        List<Category> categories = categoryRepository.findByType(type);
        List<CategoryResponse> responseList = new ArrayList<>();

        for (Category category : categories) {
            responseList.add(new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getType()
            ));
        }

        return responseList;
    }

    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category(request.getName().trim(), request.getType());
        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getType());
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = getCategoryEntityById(id);
        category.setName(request.getName().trim());
        category.setType(request.getType());
        Category updated = categoryRepository.save(category);
        return new CategoryResponse(updated.getId(), updated.getName(), updated.getType());
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
