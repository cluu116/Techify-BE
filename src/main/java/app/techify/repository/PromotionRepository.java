package app.techify.repository;

import app.techify.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findAllByIsDeletedFalse();
    Optional<Promotion> findByIdAndIsDeletedFalse(Integer id);
}
