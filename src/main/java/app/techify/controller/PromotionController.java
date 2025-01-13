package app.techify.controller;

import app.techify.entity.Promotion;
import app.techify.service.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/create")
    public ResponseEntity<?> createPromotion(@Valid @RequestBody Promotion promotion) {
        String result = promotionService.createPromotion(promotion);
        if (result.startsWith("Lỗi:")) {
            return ResponseEntity.badRequest().body(result);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        try {
            List<Promotion> promotions = promotionService.getAllPromotions();
            if (promotions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(promotions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable("id") Integer id) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            return new ResponseEntity<>(promotion, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable("id") Integer id, @RequestBody Promotion promotion) {
        promotion.setId(id);
        String result = promotionService.updatePromotion(promotion);
        if (result.startsWith("Lỗi:")) {
            if (result.contains("Không tìm thấy khuyến mãi")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } else {
            return ResponseEntity.ok().body(result);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deletePromotion(@PathVariable("id") Integer id) {
        try {
            promotionService.deletePromotion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{promotionId}/products")
    public ResponseEntity<?> addProductsToPromotion(
            @PathVariable("promotionId") Integer promotionId,
            @RequestBody List<String> productIds) {
        try {
            promotionService.addProductsToPromotion(promotionId, productIds);
            return ResponseEntity.ok().body("Sản phẩm đã được thêm vào khuyến mãi thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm sản phẩm vào khuyến mãi: " + e.getMessage());
        }
    }
}