package app.techify.repository;

import app.techify.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Short> {
    List<PaymentMethod> findAllByIsDeletedFalse();
    Optional<PaymentMethod> findByIdAndIsDeletedFalse(Short id);
} 