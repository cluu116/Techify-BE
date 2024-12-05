package app.techify.service;

import app.techify.dto.TopSellingProductResponse;
import app.techify.entity.TopSellingProduct;
import app.techify.entity.ProductPromotion;
import app.techify.entity.Promotion;
import app.techify.repository.TopSellingProductRepository;
import app.techify.repository.ProductPromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopSellingProductService {
    private final TopSellingProductRepository topSellingProductRepository;
    private final ProductPromotionRepository productPromotionRepository;

    public List<TopSellingProductResponse> getTopSellingProducts() {
        List<TopSellingProduct> topProducts = topSellingProductRepository.findAll();
        return topProducts.stream()
            .map(this::convertToResponse)
            .limit(8)
            .collect(Collectors.toList());
    }

    private TopSellingProductResponse convertToResponse(TopSellingProduct product) {
        TopSellingProductResponse response = new TopSellingProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setThumbnail(product.getThumbnail());
        response.setCategoryName(product.getCategoryName());
        response.setSellPrice(product.getSellPrice());
        response.setTotalQuantitySold(product.getTotalQuantitySold());
        response.setNumberOfOrders(product.getNumberOfOrders());
        response.setTotalRevenue(product.getTotalRevenue());
        
        // Calculate promotion price
        BigDecimal promotionPrice = calculatePromotionPrice(product.getProductId(), product.getSellPrice());
        response.setPromotionPrice(promotionPrice);
        
        return response;
    }

    private BigDecimal calculatePromotionPrice(String productId, BigDecimal sellPrice) {
        List<ProductPromotion> productPromotions = productPromotionRepository.findByProductIdWithPromotion(productId);
        BigDecimal lowestPrice = sellPrice;

        for (ProductPromotion pp : productPromotions) {
            Promotion promotion = pp.getPromotion();
            if (isPromotionActive(promotion)) {
                BigDecimal discountedPrice;
                if (promotion.getDiscountType()) {
                    // Percentage discount
                    BigDecimal discountFactor = BigDecimal.ONE.subtract(
                        BigDecimal.valueOf(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100)));
                    discountedPrice = sellPrice.multiply(discountFactor);
                } else {
                    // Fixed amount discount
                    discountedPrice = sellPrice.subtract(BigDecimal.valueOf(promotion.getDiscountValue()));
                }
                discountedPrice = discountedPrice.max(BigDecimal.ZERO);
                if (discountedPrice.compareTo(lowestPrice) < 0) {
                    lowestPrice = discountedPrice;
                }
            }
        }
        return lowestPrice;
    }

    private boolean isPromotionActive(Promotion promotion) {
        Instant now = Instant.now();
        return now.isAfter(promotion.getStartDate()) && now.isBefore(promotion.getEndDate());
    }
} 