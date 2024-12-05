package app.techify.service;

import app.techify.dto.UserResponse;
import app.techify.entity.Account;
import app.techify.entity.Customer;
import app.techify.repository.AccountRepository;
import app.techify.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    public Account createAccount(Account account) {
        if (account.getPasswordHash() != null) {
            account.setPasswordHash(passwordEncoder.encode(account.getPasswordHash()));
        }
        if (account.getRole() == null) {
            account.setRole("CUSTOMER");
        }
        return accountRepository.save(account);
    }
    public UserResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Chưa xác thực người dùng");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Account account = accountRepository.findByEmail(username).orElseThrow();
        Customer customer = customerRepository.findCustomerByAccount(account);

        return UserResponse.builder()
                .fullName(customer.getFullName())
                .role(account.getRole())
                .province(customer.getProvince())
                .district(customer.getDistrict())
                .ward(customer.getWard())
                .address(customer.getAddress())
                .altAddress(customer.getAltAddress())
                .phone(customer.getPhone())
                .altPhone(customer.getAltPhone())
                .email(username)
                .build();
    }
}
