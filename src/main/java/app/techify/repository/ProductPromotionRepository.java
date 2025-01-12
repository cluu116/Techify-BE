package app.techify.repository;

import app.techify.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Integer> {
    @Query("SELECT pp FROM ProductPromotion pp JOIN FETCH pp.promotion WHERE pp.product.id = :productId AND pp.isDeleted = false")
    List<ProductPromotion> findByProductIdWithPromotion(@Param("productId") String productId);

    @Modifying
    @Query("DELETE FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId")
    void deleteByPromotionId(Integer promotionId);

    @Modifying
    @Query("UPDATE ProductPromotion pp SET pp.isDeleted = true WHERE pp.promotion.id = :promotionId")
    void softDeleteByPromotionId(@Param("promotionId") Integer promotionId);

    @Query("SELECT pp.product.id FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.isDeleted = false")
    List<String> findProductIdsByPromotionId(@Param("promotionId") Integer promotionId);
}