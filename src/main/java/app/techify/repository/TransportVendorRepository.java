package app.techify.repository;

import app.techify.entity.TransportVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportVendorRepository extends JpaRepository<TransportVendor, String> {
    List<TransportVendor> findAllByStatusTrue();
    @Query("SELECT MAX(CAST(SUBSTRING(tv.id, LENGTH(:baseId) + 1) AS int)) FROM TransportVendor tv WHERE tv.id LIKE CONCAT(:baseId, '%')")
    Optional<Integer> findMaxSequenceForDate(@Param("baseId") String baseId);
} 