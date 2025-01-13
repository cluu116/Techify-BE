package app.techify.controller;

import app.techify.dto.ProductDto;
import app.techify.service.ProductPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-promotions")
public class ProductPromotionController {

    @Autowired
    private ProductPromotionService productPromotionService;

    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<?> getProductsByPromotionId(@PathVariable Integer promotionId) {
        try {
            List<ProductDto> products = productPromotionService.getActiveProductsByPromotionId(promotionId);
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
        }
    }

    @GetMapping("/promotion/{promotionId}/not-included")
    public ResponseEntity<?> getProductsNotInPromotion(@PathVariable Integer promotionId) {
        try {
            List<ProductDto> products = productPromotionService.getProductsNotInPromotion(promotionId);
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách sản phẩm không thuộc khuyến mãi: " + e.getMessage());
        }
    }

    @DeleteMapping("/promotion/{promotionId}/product/{productId}")
    public ResponseEntity<?> removeProductFromPromotion(
            @PathVariable Integer promotionId,
            @PathVariable String productId) {
        try {
            productPromotionService.removeProductFromPromotion(promotionId, productId);
            return ResponseEntity.ok("Sản phẩm đã được xóa khỏi khuyến mãi thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa sản phẩm khỏi khuyến mãi: " + e.getMessage());
        }
    }
}