package app.techify.service;

import app.techify.entity.Account;
import app.techify.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdminAccount() {
        // Check if any admin account exists
        boolean adminExists = accountRepository.existsByRole("ADMIN");
        
        if (!adminExists) {
            // Create admin account
            Account adminAccount = Account.builder()
                    .email("admin")
                    .passwordHash(passwordEncoder.encode("admin"))
                    .role("ADMIN")
                    .build();
            
            accountRepository.save(adminAccount);
            log.info("Admin account has been initialized");
        }
    }
} 