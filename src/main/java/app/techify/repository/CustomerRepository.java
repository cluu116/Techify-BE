package app.techify.repository;

import app.techify.entity.Account;
import app.techify.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findCustomerByAccount(Account account);
}
