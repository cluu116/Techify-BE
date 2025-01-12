package app.techify.repository;

import app.techify.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {
    List<Voucher> findAllByIsDeletedFalse();
    Optional<Voucher> findByIdAndIsDeletedFalse(String id);
    boolean existsByIdAndIsDeletedFalse(String id);
} 