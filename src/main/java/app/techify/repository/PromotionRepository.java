package app.techify.repository;

import app.techify.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findAllByIsDeletedFalse();
    Optional<Promotion> findByIdAndIsDeletedFalse(Integer id);
    @Query("SELECT p FROM Promotion p WHERE p.isDeleted = false " +
            "AND ((p.startDate BETWEEN :start AND :end) " +
            "OR (p.endDate BETWEEN :start AND :end) " +
            "OR (:start BETWEEN p.startDate AND p.endDate)) " +
            "AND p.id != :excludeId")
    List<Promotion> findOverlappingPromotions(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("excludeId") Integer excludeId
    );
}
