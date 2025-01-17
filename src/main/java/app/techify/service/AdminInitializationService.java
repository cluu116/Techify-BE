package app.techify.service;

import app.techify.entity.Account;
import app.techify.entity.Staff;
import app.techify.repository.AccountRepository;
import app.techify.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializationService {

    private final AccountRepository accountRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeAdminAccount() {
        // Check if any admin account exists
        boolean adminExists = accountRepository.existsByRole("ADMIN");

        if (!adminExists) {
            // Create admin account
            Account adminAccount = Account.builder()
                    .email("techifyshop22@gmail.com")
                    .passwordHash(passwordEncoder.encode("Techify@123"))
                    .role("ADMIN")
                    .avatar("https://lh3.googleusercontent.com/a/ACg8ocJ7Vok9rLRX4rrkvebCcZBiYFZM9d3_2m-L_LHmPtBwIL4XbjaX=s96-c")
                    .isDeleted(false)
                    .build();

            adminAccount = accountRepository.save(adminAccount);

            // Create associated Staff
            Staff adminStaff = Staff.builder()
                    .id("ADM-001")
                    .account(adminAccount)
                    .fullName("Admin")
                    .dob(LocalDate.of(2004, 6, 11))
                    .gender("Nam")
                    .citizenId("001204041240")
                    .phone("0867110604")
                    .address("Thôn Đìa")
                    .province("01")
                    .district("278")
                    .ward("10135")
                    .build();

            staffRepository.save(adminStaff);

            log.info("Admin account and associated Staff have been initialized");
        }
    }
}