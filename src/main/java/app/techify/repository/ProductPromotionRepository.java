package app.techify.repository;

import app.techify.entity.Product;
import app.techify.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Integer> {
    @Query("SELECT pp FROM ProductPromotion pp JOIN FETCH pp.promotion WHERE pp.product.id = :productId AND pp.isDeleted = false")
    List<ProductPromotion> findByProductIdWithPromotion(@Param("productId") String productId);

    @Query("SELECT pp FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.product.id = :productId AND pp.isDeleted = false")
    Optional<ProductPromotion> findByPromotionIdAndProductId(@Param("promotionId") Integer promotionId, @Param("productId") String productId);

    @Modifying
    @Query("UPDATE ProductPromotion pp SET pp.isDeleted = true WHERE pp.promotion.id = :promotionId AND pp.product.id = :productId")
    int softDeleteByPromotionIdAndProductId(@Param("promotionId") Integer promotionId, @Param("productId") String productId);

    @Modifying
    @Query("UPDATE ProductPromotion pp SET pp.isDeleted = true WHERE pp.promotion.id = :promotionId")
    void softDeleteByPromotionId(@Param("promotionId") Integer promotionId);

    @Query("SELECT pp.product.id FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.isDeleted = false")
    List<String> findProductIdsByPromotionId(@Param("promotionId") Integer promotionId);

    @Query("SELECT pp.product FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.isDeleted = false AND pp.product.isDeleted = false")
    List<Product> findActiveProductsByPromotionId(@Param("promotionId") Integer promotionId);

    @Query("SELECT p FROM Product p WHERE p.id NOT IN (SELECT pp.product.id FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.isDeleted = false) AND p.isDeleted = false")
    List<Product> findActiveProductsNotInPromotion(@Param("promotionId") Integer promotionId);
}