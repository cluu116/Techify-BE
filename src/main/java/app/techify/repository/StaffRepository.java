package app.techify.repository;

import app.techify.entity.Account;
import app.techify.entity.Customer;
import app.techify.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    Staff findStaffByAccount(Account account);
} 