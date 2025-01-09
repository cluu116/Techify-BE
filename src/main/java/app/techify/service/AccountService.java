package app.techify.service;

import app.techify.dto.UserResponse;
import app.techify.entity.Account;
import app.techify.entity.Customer;
import app.techify.entity.Staff;
import app.techify.repository.AccountRepository;
import app.techify.repository.CustomerRepository;
import app.techify.repository.StaffRepository;
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
    private final StaffRepository staffRepository;
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
        UserResponse.UserResponseBuilder responseBuilder = UserResponse.builder()
                .email(username)
                .role(account.getRole())
                .avatar(account.getAvatar());
        if ("CUSTOMER".equals(account.getRole())) {
            Customer customer = customerRepository.findCustomerByAccount(account);
            if (customer != null) {
                responseBuilder
                        .id(customer.getId())
                        .fullName(customer.getFullName())
                        .province(customer.getProvince())
                        .district(customer.getDistrict())
                        .ward(customer.getWard())
                        .address(customer.getAddress())
                        .phone(customer.getPhone());
            }
        } else if ("STAFF".equals(account.getRole()) || "ADMIN".equals(account.getRole())) {
            Staff staff = staffRepository.findStaffByAccount(account);
            if (staff != null) {
                responseBuilder
                        .id(staff.getId())
                        .fullName(staff.getFullName())
                        .phone(staff.getPhone())
                        .province(staff.getProvince())
                        .district(staff.getDistrict())
                        .ward(staff.getWard())
                        .address(staff.getAddress())
                        .gender(staff.getGender())
                        .dob(staff.getDob())
                        .citizenId(staff.getCitizenId());
            }
        }
        return responseBuilder.build();
    }
}
