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
        promotion.setIsDeleted(false);
        try {
            return promotionRepository.save(promotion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create promotion: " + e.getMessage(), e);
        }
    }

    // Retrieve all promotions
    public List<Promotion> getAllPromotions() {
        try {
            return promotionRepository.findAllByIsDeletedFalse();
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve promotions: " + e.getMessage(), e);
        }
    }

    // Retrieve a promotion by ID
    public Promotion getPromotionById(Integer id) {
        try {
            return promotionRepository.findByIdAndIsDeletedFalse(id)
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
            Promotion existingPromotion = promotionRepository.findByIdAndIsDeletedFalse(promotion.getId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotion.getId()));

            existingPromotion.setName(promotion.getName());
            existingPromotion.setDescription(promotion.getDescription());
            existingPromotion.setDiscountType(promotion.getDiscountType());
            existingPromotion.setDiscountValue(promotion.getDiscountValue());
            existingPromotion.setStartDate(promotion.getStartDate());
            existingPromotion.setEndDate(promotion.getEndDate());

            return promotionRepository.save(existingPromotion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to update promotion: " + e.getMessage(), e);
        }
    }

    // Delete a promotion
    @Transactional
    public void deletePromotion(Integer id) {
        try {
            Promotion promotion = promotionRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));

            promotion.setIsDeleted(true);
            promotionRepository.save(promotion);

            productPromotionRepository.softDeleteByPromotionId(id);
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
        Promotion promotion = promotionRepository.findByIdAndIsDeletedFalse(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));

        // Kiểm tra xem promotion có đang hoạt động không
        if (promotion.getEndDate().isBefore(Instant.now())) {
            throw new RuntimeException("Cannot add products to an expired promotion");
        }

        // Lọc ra các sản phẩm chưa có trong promotion
        List<String> existingProductIds = productPromotionRepository.findProductIdsByPromotionId(promotionId);
        List<String> newProductIds = productIds.stream()
                .filter(id -> !existingProductIds.contains(id))
                .collect(Collectors.toList());

        if (newProductIds.isEmpty()) {
            return;
        }

        List<ProductPromotion> productPromotions = newProductIds.stream()
                .map(productId -> ProductPromotion.builder()
                        .product(Product.builder().id(productId).build())
                        .promotion(promotion)
                        .isDeleted(false)
                        .build())
                .collect(Collectors.toList());

        productPromotionRepository.saveAll(productPromotions);
    }
}