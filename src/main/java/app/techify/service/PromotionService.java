package app.techify.service;

import app.techify.entity.Product;
import app.techify.entity.ProductPromotion;
import app.techify.entity.Promotion;
import app.techify.repository.ProductPromotionRepository;
import app.techify.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductPromotionRepository productPromotionRepository;


    // Create a new promotion
    public Promotion createPromotion(Promotion promotion) {
        validatePromotionDates(promotion);
        validateDiscountValues(promotion);
        try {
            return promotionRepository.save(promotion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create promotion: " + e.getMessage(), e);
        }
    }

    // Retrieve all promotions
    public List<Promotion> getAllPromotions() {
        try {
            return promotionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve promotions: " + e.getMessage(), e);
        }
    }

    // Retrieve a promotion by ID
    public Promotion getPromotionById(Integer id) {
        try {
            return promotionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving promotion: " + e.getMessage(), e);
        }
    }

    // Update an existing promotion
    public Promotion updatePromotion(Promotion promotion) {
        validatePromotionDates(promotion);
        validateDiscountValues(promotion);
        try {
            if (!promotionRepository.existsById(promotion.getId())) {
                throw new RuntimeException("Promotion not found with ID: " + promotion.getId());
            }
            return promotionRepository.save(promotion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to update promotion: " + e.getMessage(), e);
        }
    }

    // Delete a promotion
    @Transactional
    public void deletePromotion(Integer id) {
        try {
            if (!promotionRepository.existsById(id)) {
                throw new RuntimeException("Promotion not found with ID: " + id);
            }

            // Xóa tất cả các bản ghi liên quan trong bảng productPromotion
            productPromotionRepository.deleteByPromotionId(id);

            // Xóa promotion
            promotionRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete promotion: " + e.getMessage(), e);
        }
    }

    private void validatePromotionDates(Promotion promotion) {
        Instant now = Instant.now();
        
        if (promotion.getStartDate().isBefore(now)) {
            throw new RuntimeException("Start date cannot be in the past");
        }
        
        if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
    }

    private void validateDiscountValues(Promotion promotion) {
        if (promotion.getDiscountValue() <= 0) {
            throw new RuntimeException("Discount value must be greater than 0");
        }

        if (promotion.getDiscountType()) { // Percentage discount
            if (promotion.getDiscountValue() > 100) {
                throw new RuntimeException("Percentage discount cannot be greater than 100%");
            }
        }
    }

    @Transactional
    public void addProductsToPromotion(Integer promotionId, List<String> productIds) {
        // Verify promotion exists
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));

        // Create ProductPromotion entries
        List<ProductPromotion> productPromotions = productIds.stream()
                .map(productId -> ProductPromotion.builder()
                        .product(Product.builder().id(productId).build())
                        .promotion(promotion)
                        .build())
                .collect(Collectors.toList());

        // Save all entries
        productPromotionRepository.saveAll(productPromotions);
    }
}