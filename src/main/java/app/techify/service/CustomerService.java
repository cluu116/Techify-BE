package app.techify.service;

import app.techify.dto.CustomerDto;
import app.techify.entity.Customer;
import app.techify.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    public void createCustomer(Customer customer) {
        String epoch = String.valueOf(System.currentTimeMillis());
        customer.setId("C_" + epoch);
        customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(String customerId, CustomerDto updateDto) {
        Optional<Customer> existingCustomerOpt = customerRepository.findById(customerId);

        if (existingCustomerOpt.isPresent()) {
            Customer existingCustomer = existingCustomerOpt.get();

            if (updateDto.getFullName() != null) {
                existingCustomer.setFullName(updateDto.getFullName());
            }
            if (updateDto.getPhone() != null) {
                existingCustomer.setPhone(updateDto.getPhone());
            }
            if (updateDto.getProvince() != null) {
                existingCustomer.setProvince(updateDto.getProvince());
            }
            if (updateDto.getDistrict() != null) {
                existingCustomer.setDistrict(updateDto.getDistrict());
            }
            if (updateDto.getWard() != null) {
                existingCustomer.setWard(updateDto.getWard());
            }
            if (updateDto.getAddress() != null) {
                existingCustomer.setAddress(updateDto.getAddress());
            }

            return customerRepository.save(existingCustomer);
        } else {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
    }
}
