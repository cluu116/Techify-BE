package app.techify.service;

import app.techify.entity.Account;
import app.techify.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Xử lý trường hợp mật khẩu null (đăng nhập bằng Google)
        String password = account.getPasswordHash() != null ? account.getPasswordHash() : "";

        return User.builder()
                .username(account.getEmail())
                .password(password)
                .roles(account.getRole())
                .build();
    }
}