package app.techify.repository;

import app.techify.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.color " +
            "LEFT JOIN FETCH p.image " +
            "LEFT JOIN FETCH p.attribute " +
            "WHERE p.id = :id AND p.isDeleted = false")
    Optional<Product> findByIdWithDetails(@Param("id") String id);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.color " +
            "LEFT JOIN FETCH p.image " +
            "LEFT JOIN FETCH p.attribute " +
            "WHERE p.isDeleted = false")
    List<Product> findAllWithDetails();

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.color " +
            "LEFT JOIN FETCH p.image " +
            "LEFT JOIN FETCH p.attribute " +
            "WHERE p.category.id = :categoryId AND p.isDeleted = false")
    Page<Product> findByCategoryIdWithDetails(
            @Param("categoryId") Integer categoryId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.category.id = :categoryId AND p.isDeleted = false")
    List<String> findDistinctBrandsByCategory_Id(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.color LEFT JOIN FETCH p.image LEFT JOIN FETCH p.attribute WHERE p.category.id = :categoryId AND p.brand IN :brands AND p.isDeleted = false")
    Page<Product> findByCategoryIdAndBrandsWithDetails(@Param("categoryId") Integer categoryId, @Param("brands") List<String> brands, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.color " +
            "LEFT JOIN FETCH p.image " +
            "LEFT JOIN FETCH p.attribute " +
            "WHERE p.category.id = :categoryId " +
            "AND (:brands IS NULL OR p.brand IN :brands) " +
            "AND (:attributeFilters IS NULL OR p.attribute.attributeJson LIKE :attributeFilters) " +
            "AND p.isDeleted = false")
    Page<Product> findByCategoryIdAndFiltersWithDetails(
            @Param("categoryId") Integer categoryId,
            @Param("brands") List<String> brands,
            @Param("attributeFilters") String attributeFilters,
            Pageable pageable
    );


    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.color " +
            "LEFT JOIN FETCH p.image " +
            "LEFT JOIN FETCH p.attribute " +
            "WHERE p.category.id = :categoryId AND p.isDeleted = false")
    List<Product> findAllByCategoryIdWithDetails(@Param("categoryId") Integer categoryId);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.color " +
            "LEFT JOIN FETCH p.image " +
            "LEFT JOIN FETCH p.attribute " +
            "WHERE p.category.id = :categoryId AND p.brand IN :brands AND p.isDeleted = false")
    List<Product> findAllByCategoryIdAndBrandsWithDetails(
            @Param("categoryId") Integer categoryId,
            @Param("brands") List<String> brands
    );

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.orderDetails od " +
            "WHERE p.isDeleted = false " +
            "GROUP BY p " +
            "ORDER BY COALESCE(SUM(od.quantity), 0) DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    List<Product> findByCategoryIdAndIdNotAndIsDeletedFalse(Integer categoryId, String productId);

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isDeleted = false")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:brands IS NULL OR p.brand IN :brands) " +
            "AND (:minPrice IS NULL OR p.sellPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.sellPrice <= :maxPrice) " +
            "AND p.isDeleted = false")
    Page<Product> filterProducts(@Param("categoryId") Integer categoryId,
                                 @Param("brands") List<String> brands,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

    Optional<Product> findByIdAndIsDeletedFalse(String id);
}
