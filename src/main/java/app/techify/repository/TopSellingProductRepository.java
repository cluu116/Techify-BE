package app.techify.repository;

import app.techify.entity.TopSellingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopSellingProductRepository extends JpaRepository<TopSellingProduct, String> {
} 