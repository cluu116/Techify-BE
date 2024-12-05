package app.techify.service;

import app.techify.entity.Customer;
import app.techify.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    public void createCustomer(Customer customer) {
        String epoch = String.valueOf(System.currentTimeMillis());
        customer.setId("C_" + epoch);
        customerRepository.save(customer);
    }
}
