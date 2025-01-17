package app.techify.controller;

import app.techify.dto.CustomerDto;
import app.techify.entity.Customer;
import app.techify.entity.Staff;
import app.techify.service.CustomerService;
import app.techify.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final StaffService staffService;
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @RequestBody CustomerDto updateDTO) {
        Customer updatedCustomer = customerService.updateCustomer(id, updateDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/check-phone/{phone}")
    public ResponseEntity<Map<String, Object>> checkPhone(@PathVariable String phone) {
        Customer customer = customerService.findByPhone(phone);
        Staff staff = staffService.findByPhone(phone);

        Map<String, Object> response = new HashMap<>();
        boolean exists = customer != null || staff != null;
        String userId = null;

        if (customer != null) {
            userId = customer.getId();
        } else if (staff != null) {
            userId = staff.getId();
        }

        response.put("exists", exists);
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }
}
