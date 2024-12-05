package app.techify.repository;

import app.techify.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    boolean existsByRole(String role);

    Optional<Account> findByGoogleId(String googleId);

    Optional<Account> findByFacebookId(String facebookId);
}