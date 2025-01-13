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
    public String createPromotion(Promotion promotion) {
        try {
            validatePromotionDates(promotion);
            validateDiscountValues(promotion);
            checkOverlappingPromotions(promotion);
            promotion.setIsDeleted(false);
            promotionRepository.save(promotion);
            return "Tạo khuyến mãi thành công";
        } catch (IllegalStateException e) {
            return "Lỗi: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Lỗi: Không thể tạo khuyến mãi - " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi không xác định khi tạo khuyến mãi: " + e.getMessage();
        }
    }

    private void checkOverlappingPromotions(Promotion newPromotion) {
        List<Promotion> overlappingPromotions = promotionRepository.findOverlappingPromotions(
                newPromotion.getStartDate(),
                newPromotion.getEndDate(),
                newPromotion.getId() == null ? -1 : newPromotion.getId()
        );

        if (!overlappingPromotions.isEmpty()) {
            throw new IllegalStateException("Đã tồn tại khuyến mãi trong khoảng thời gian này. Vui lòng chọn khoảng thời gian khác.");
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
    public String updatePromotion(Promotion promotion) {
        try {
            validatePromotionDates(promotion);
            validateDiscountValues(promotion);

            Promotion existingPromotion = promotionRepository.findByIdAndIsDeletedFalse(promotion.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + promotion.getId()));

            if (!existingPromotion.getStartDate().equals(promotion.getStartDate()) ||
                    !existingPromotion.getEndDate().equals(promotion.getEndDate())) {
                List<Promotion> overlappingPromotions = promotionRepository.findOverlappingPromotions(
                        promotion.getStartDate(),
                        promotion.getEndDate(),
                        promotion.getId()
                );
                if (!overlappingPromotions.isEmpty()) {
                    return "Lỗi: Đã tồn tại khuyến mãi trong khoảng thời gian này. Vui lòng chọn khoảng thời gian khác.";
                }
            }

            existingPromotion.setName(promotion.getName());
            existingPromotion.setDescription(promotion.getDescription());
            existingPromotion.setDiscountType(promotion.getDiscountType());
            existingPromotion.setDiscountValue(promotion.getDiscountValue());
            existingPromotion.setStartDate(promotion.getStartDate());
            existingPromotion.setEndDate(promotion.getEndDate());

            promotionRepository.save(existingPromotion);
            return "Cập nhật khuyến mãi thành công";
        } catch (IllegalStateException e) {
            return "Lỗi: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Lỗi: Không thể cập nhật khuyến mãi - " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi không xác định khi cập nhật khuyến mãi: " + e.getMessage();
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
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách sản phẩm không được để trống.");
        }

        Promotion promotion = promotionRepository.findByIdAndIsDeletedFalse(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + promotionId));

        // Kiểm tra xem promotion có đang hoạt động không
        if (promotion.getEndDate().isBefore(Instant.now())) {
            throw new IllegalStateException("Không thể thêm sản phẩm vào khuyến mãi đã hết hạn.");
        }

        // Lọc ra các sản phẩm chưa có trong promotion
        List<String> existingProductIds = productPromotionRepository.findProductIdsByPromotionId(promotionId);
        List<String> newProductIds = productIds.stream()
                .filter(id -> !existingProductIds.contains(id))
                .collect(Collectors.toList());

        if (newProductIds.isEmpty()) {
            throw new IllegalStateException("Tất cả sản phẩm đã tồn tại trong khuyến mãi này.");
        }

        List<ProductPromotion> productPromotions = newProductIds.stream()
                .map(productId -> ProductPromotion.builder()
                        .product(Product.builder().id(productId).build())
                        .promotion(promotion)
                        .isDeleted(false)
                        .build())
                .collect(Collectors.toList());

        try {
            productPromotionRepository.saveAll(productPromotions);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm vào khuyến mãi: " + e.getMessage(), e);
        }
    }
}