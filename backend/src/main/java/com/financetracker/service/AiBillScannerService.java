package com.financetracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.dto.AiBillScanResponse;
import com.financetracker.entity.Category;
import com.financetracker.entity.CategoryType;
import com.financetracker.entity.TransactionType;
import com.financetracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to scan and categorize bill receipts and statements
 * using Google Gemini AI.
 * 
 * Beginner-friendly design: Uses Spring's built-in RestTemplate
 * and Jackson ObjectMapper for transparent, simple HTTP calls.
 */
@Service
public class AiBillScannerService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String modelName;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiBillScannerService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Scan an uploaded bill/receipt image (JPG, PNG, WEBP, etc.)
     */
    public AiBillScanResponse scanReceiptImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please provide a valid receipt or bill image file.");
        }

        try {
            // 1. Convert image bytes to Base64
            byte[] fileBytes = file.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);
            String mimeType = file.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg";
            }

            // 2. Fetch available categories from database to provide in prompt
            List<Category> allCategories = categoryRepository.findAll();
            String categoriesListStr = formatCategoriesForPrompt(allCategories);

            // 3. Build system instruction prompt
            String promptText = buildPrompt(categoriesListStr, "the provided bill/receipt image");

            // 4. Construct Gemini Multimodal Request Payload
            Map<String, Object> textPart = Map.of("text", promptText);
            Map<String, Object> inlineData = Map.of(
                    "mimeType", mimeType,
                    "data", base64Data
            );
            Map<String, Object> imagePart = Map.of("inlineData", inlineData);

            Map<String, Object> content = Map.of("parts", List.of(textPart, imagePart));
            Map<String, Object> generationConfig = Map.of("responseMimeType", "application/json");

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(content),
                    "generationConfig", generationConfig
            );

            // 5. Call Gemini API and process response
            return callGeminiAndParseResponse(requestBody, allCategories);

        } catch (Exception e) {
            throw new RuntimeException("Error scanning bill image with Gemini AI: " + e.getMessage(), e);
        }
    }

    /**
     * Scan a text-based bill statement, invoice text, or SMS alert
     */
    public AiBillScanResponse scanBillStatementText(String statementText) {
        if (statementText == null || statementText.trim().isEmpty()) {
            throw new IllegalArgumentException("Please provide bill statement text.");
        }

        try {
            // 1. Fetch available categories from database
            List<Category> allCategories = categoryRepository.findAll();
            String categoriesListStr = formatCategoriesForPrompt(allCategories);

            // 2. Build prompt including the statement text
            String promptText = buildPrompt(categoriesListStr, "the following bill statement / receipt text:\n\"\"\"\n" + statementText + "\n\"\"\"");

            // 3. Construct Gemini Text Request Payload
            Map<String, Object> textPart = Map.of("text", promptText);
            Map<String, Object> content = Map.of("parts", List.of(textPart));
            Map<String, Object> generationConfig = Map.of("responseMimeType", "application/json");

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(content),
                    "generationConfig", generationConfig
            );

            // 4. Call Gemini API and process response
            return callGeminiAndParseResponse(requestBody, allCategories);

        } catch (Exception e) {
            throw new RuntimeException("Error analyzing bill statement with Gemini AI: " + e.getMessage(), e);
        }
    }

    /**
     * Sends the HTTP POST request to Google Gemini and parses the structured JSON output.
     */
    private AiBillScanResponse callGeminiAndParseResponse(Map<String, Object> requestBody, List<Category> categories) throws Exception {
        String endpoint = String.format("%s/%s:generateContent?key=%s", apiUrl, modelName, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Gemini API returned status: " + response.getStatusCode());
        }

        // Parse Gemini top-level JSON response
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isEmpty()) {
            throw new RuntimeException("Gemini AI could not detect any readable transaction data in this bill.");
        }

        String rawJsonText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();

        // Parse extracted JSON from the model
        JsonNode parsedBill = objectMapper.readTree(rawJsonText);

        String title = parsedBill.path("title").asText("Bill Payment");
        double amountDouble = parsedBill.path("amount").asDouble(0.0);
        BigDecimal amount = BigDecimal.valueOf(amountDouble).setScale(2, java.math.RoundingMode.HALF_UP);

        String typeStr = parsedBill.path("type").asText("EXPENSE").toUpperCase();
        TransactionType transactionType = "INCOME".equalsIgnoreCase(typeStr) ? TransactionType.INCOME : TransactionType.EXPENSE;

        String suggestedCategory = parsedBill.path("category").asText("Other");
        String dateStr = parsedBill.path("date").asText("");
        String description = parsedBill.path("description").asText("");
        String summary = parsedBill.path("summary").asText("Extracted with Gemini AI");

        // Resolve Category to Database Category ID & Name
        Category matchedCategory = findBestMatchingCategory(suggestedCategory, categories, transactionType);

        // Resolve Date (fallback to today if unparseable)
        LocalDate transactionDate = parseDateOrDefault(dateStr);

        return new AiBillScanResponse(
                title,
                amount,
                transactionType,
                matchedCategory != null ? matchedCategory.getId() : null,
                matchedCategory != null ? matchedCategory.getName() : suggestedCategory,
                transactionDate,
                description,
                summary
        );
    }

    /**
     * Constructs a clean, strict prompt for Gemini to return a structured JSON object.
     */
    private String buildPrompt(String categoriesListStr, String inputDescription) {
        return "You are an intelligent financial assistant for a Personal Finance Tracker application.\n"
                + "Analyze " + inputDescription + " and extract the transaction details.\n\n"
                + "Available Categories in the user's database are:\n"
                + categoriesListStr + "\n\n"
                + "Instructions:\n"
                + "1. Extract the merchant/service name or vendor for 'title'. Keep it clean and concise (e.g. 'Starbucks Coffee', 'Electricity Bill', 'Uber Ride', 'Walmart').\n"
                + "2. Extract the total final amount as a numeric value for 'amount' (e.g. 24.50 or 450.00). Do not include currency symbols in the number.\n"
                + "3. Determine if it is 'EXPENSE' or 'INCOME' for 'type' (almost always 'EXPENSE' for bills/receipts).\n"
                + "4. Categorize this bill by choosing the closest matching category from the Available Categories list above for 'category'.\n"
                + "5. Extract the date in 'YYYY-MM-DD' format for 'date'. If no date is found, return null.\n"
                + "6. Provide a clean summary of purchased items or service details for 'description'.\n"
                + "7. Provide a short 1-sentence explanation of why this category was chosen for 'summary'.\n\n"
                + "Return ONLY a valid JSON object matching this exact schema:\n"
                + "{\n"
                + "  \"title\": \"string\",\n"
                + "  \"amount\": number,\n"
                + "  \"type\": \"EXPENSE\" | \"INCOME\",\n"
                + "  \"category\": \"string\",\n"
                + "  \"date\": \"YYYY-MM-DD\" | null,\n"
                + "  \"description\": \"string\",\n"
                + "  \"summary\": \"string\"\n"
                + "}";
    }

    /**
     * Formats category names with their type for prompt context
     */
    private String formatCategoriesForPrompt(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return "- Food, Transport, Shopping, Bills, Entertainment, Education, Healthcare, Other (EXPENSE)\n- Salary, Freelance, Investments (INCOME)";
        }

        return categories.stream()
                .map(c -> "- " + c.getName() + " (" + c.getType() + ")")
                .collect(Collectors.joining("\n"));
    }

    /**
     * Matches the AI suggested category string with existing database categories.
     */
    private Category findBestMatchingCategory(String suggestedCategory, List<Category> categories, TransactionType type) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        String cleanSuggested = suggestedCategory != null ? suggestedCategory.trim().toLowerCase() : "";

        // 1. Exact match (case-insensitive)
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(cleanSuggested)) {
                return c;
            }
        }

        // 2. Partial match (e.g. "Groceries" -> "Food", "Electric" -> "Bills", "Taxi" -> "Transport")
        for (Category c : categories) {
            String catLower = c.getName().toLowerCase();
            if (cleanSuggested.contains(catLower) || catLower.contains(cleanSuggested)) {
                return c;
            }
        }

        // Common synonym helpers
        if (cleanSuggested.contains("grocery") || cleanSuggested.contains("restaurant") || cleanSuggested.contains("cafe") || cleanSuggested.contains("dining") || cleanSuggested.contains("coffee")) {
            return findCategoryByName(categories, "Food");
        }
        if (cleanSuggested.contains("electric") || cleanSuggested.contains("water") || cleanSuggested.contains("internet") || cleanSuggested.contains("utility") || cleanSuggested.contains("phone")) {
            return findCategoryByName(categories, "Bills");
        }
        if (cleanSuggested.contains("gas") || cleanSuggested.contains("fuel") || cleanSuggested.contains("cab") || cleanSuggested.contains("uber") || cleanSuggested.contains("metro")) {
            return findCategoryByName(categories, "Transport");
        }
        if (cleanSuggested.contains("cloth") || cleanSuggested.contains("mall") || cleanSuggested.contains("store") || cleanSuggested.contains("amazon")) {
            return findCategoryByName(categories, "Shopping");
        }
        if (cleanSuggested.contains("medical") || cleanSuggested.contains("pharmacy") || cleanSuggested.contains("doctor") || cleanSuggested.contains("hospital")) {
            return findCategoryByName(categories, "Healthcare");
        }

        // 3. Fallback: match "Other" or first category matching the transaction type
        Category otherCategory = findCategoryByName(categories, "Other");
        if (otherCategory != null) {
            return otherCategory;
        }

        CategoryType expectedType = (type == TransactionType.INCOME) ? CategoryType.INCOME : CategoryType.EXPENSE;
        return categories.stream()
                .filter(c -> c.getType() == expectedType)
                .findFirst()
                .orElse(categories.get(0));
    }

    private Category findCategoryByName(List<Category> categories, String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private LocalDate parseDateOrDefault(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || "null".equalsIgnoreCase(dateStr.trim())) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
