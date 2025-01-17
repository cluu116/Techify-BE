package app.techify.repository;

import app.techify.entity.Account;
import app.techify.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findCustomerByAccount(Account account);

    List<Customer> findByFullNameContainingIgnoreCase(String name);
    @Query("SELECT c FROM Customer c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> findCustomersByName(@Param("name") String name);

    Optional<Customer> findByAccountId(Integer accountId);
    Customer findByAccount_Email(String email);

    Optional<Customer> findByPhone(String phone);
}

