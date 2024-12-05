package app.techify.repository;

import app.techify.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Integer> {
    @Query("SELECT pp FROM ProductPromotion pp JOIN FETCH pp.promotion WHERE pp.product.id = :productId")
    List<ProductPromotion> findByProductIdWithPromotion(@Param("productId") String productId);

    @Modifying
    @Query("DELETE FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId")
    void deleteByPromotionId(Integer promotionId);
}