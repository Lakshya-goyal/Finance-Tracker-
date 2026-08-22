package com.financetracker.config;

import com.financetracker.entity.Category;
import com.financetracker.entity.CategoryType;
import com.financetracker.entity.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public DataInitializer(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            // 1. Seed Categories if empty
            if (categoryRepository.count() == 0) {
                List<Category> defaultCategories = List.of(
                        // Income categories
                        new Category("Salary", CategoryType.INCOME),
                        new Category("Freelance", CategoryType.INCOME),
                        new Category("Investments", CategoryType.INCOME),

                        // Expense categories
                        new Category("Food", CategoryType.EXPENSE),
                        new Category("Transport", CategoryType.EXPENSE),
                        new Category("Shopping", CategoryType.EXPENSE),
                        new Category("Bills", CategoryType.EXPENSE),
                        new Category("Entertainment", CategoryType.EXPENSE),
                        new Category("Education", CategoryType.EXPENSE),
                        new Category("Healthcare", CategoryType.EXPENSE),
                        new Category("Other", CategoryType.EXPENSE)
                );

                categoryRepository.saveAll(defaultCategories);
                System.out.println(">>> Seeded " + defaultCategories.size() + " default categories into database.");
            }

            // 2. Seed Default Demo User if empty
            if (userRepository.count() == 0) {
                User demoUser = new User("Demo User", "demo@example.com", "password123");
                userRepository.save(demoUser);
                System.out.println(">>> Seeded default user (demo@example.com) with ID: " + demoUser.getId());
            }
        } catch (Exception e) {
            System.err.println(">>> Notice: Could not seed initial data: " + e.getMessage());
        }
    }
}
