package app.techify.repository;

import app.techify.entity.TransportVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportVendorRepository extends JpaRepository<TransportVendor, String> {
} 