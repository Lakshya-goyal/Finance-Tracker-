package com.financetracker;

import com.financetracker.dto.AiBillScanResponse;
import com.financetracker.entity.Category;
import com.financetracker.entity.CategoryType;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.service.AiBillScannerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AiBillScannerServiceTest {

    @Autowired
    private AiBillScannerService aiBillScannerService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // Seed basic categories if not present
        if (categoryRepository.findByName("Food").isEmpty()) {
            categoryRepository.save(new Category("Food", CategoryType.EXPENSE));
        }
        if (categoryRepository.findByName("Bills").isEmpty()) {
            categoryRepository.save(new Category("Bills", CategoryType.EXPENSE));
        }
        if (categoryRepository.findByName("Transport").isEmpty()) {
            categoryRepository.save(new Category("Transport", CategoryType.EXPENSE));
        }
    }

    @Test
    void testScanBillStatementTextLive() {
        String sampleStatement = "Starbucks Coffee receipt: Paid $5.75 for 1 Iced Caramel Macchiato on 2026-08-20.";

        AiBillScanResponse response = aiBillScannerService.scanBillStatementText(sampleStatement);

        assertNotNull(response);
        assertNotNull(response.getTitle());
        assertTrue(response.getTitle().toLowerCase().contains("starbucks"));
        assertTrue(response.getAmount().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(response.getCategoryName());
        assertEquals("Food", response.getCategoryName());
        assertNotNull(response.getCategoryId());
    }

    @Test
    void testInvalidInputValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            aiBillScannerService.scanBillStatementText("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            aiBillScannerService.scanReceiptImage(null);
        });

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> {
            aiBillScannerService.scanReceiptImage(emptyFile);
        });
    }
}
