package app.techify.controller;

import app.techify.dto.*;
import app.techify.entity.Account;
import app.techify.entity.Customer;
import app.techify.repository.AccountRepository;
import app.techify.service.AccountService;
import app.techify.service.CustomerService;
import app.techify.service.FacebookAuthService;
import app.techify.service.GoogleAuthService;
import app.techify.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final CustomerService customerService;
    private final GoogleAuthService googleAuthService;
    private final FacebookAuthService facebookAuthService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String email, @RequestParam String passwordHash, @RequestParam String fullName) {
        if (accountRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng");
        }
        Account newAccount = Account.builder()
                .email(email)
                .passwordHash(passwordHash)
                .isDeleted(false)
                .build();
        Account savedAccount = accountService.createAccount(newAccount);
        Customer newCustomer = Customer.builder()
                .account(savedAccount)
                .fullName(fullName)
                .build();
        customerService.createCustomer(newCustomer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@ModelAttribute LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPasswordHash()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Account account = accountRepository.findByEmail(userDetails.getUsername()).orElseThrow();

            if (account.getIsDeleted()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản này đã bị vô hiệu hóa.");
            }

            String jwt = jwtService.generateToken(account.getEmail(), account.getRole());
            String refreshToken = jwtService.generateRefreshToken(account.getEmail(), account.getRole());

            account.setRefreshToken(refreshToken);
            accountRepository.save(account);

            return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getCurrentPassword()));

            if (authentication.isAuthenticated()) {
                Account account = accountRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

                String encodedPassword = passwordEncoder.encode(request.getNewPassword());
                account.setPasswordHash(encodedPassword);
                accountRepository.save(account);

                return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu hiện tại không đúng");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractEmail(refreshToken);
        Account account = accountRepository.findByEmail(email).orElseThrow();

        if (refreshToken.equals(account.getRefreshToken()) && !jwtService.isTokenExpired(refreshToken)) {
            String newToken = jwtService.generateToken(account.getEmail(), account.getRole());
            return ResponseEntity.ok(new AuthResponse(newToken, refreshToken));
        }

        return ResponseEntity.badRequest().body("Invalid refresh token");
    }

    @GetMapping("")
    public ResponseEntity<?> getUser() {
        try {
            return ResponseEntity.ok(accountService.getUser());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xác thực người dùng thất bại");
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<String> protectedResource() {
        return ResponseEntity.ok("Access granted to protected resource");
    }

    @GetMapping("/google")
    public ResponseEntity<?> googleAuth() {
        String authUrl = googleAuthService.createAuthorizationURL();
        return ResponseEntity.ok(Collections.singletonMap("authUrl", authUrl));
    }

    @GetMapping("/google/callback")
    @Transactional
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        try {
            // Sử dụng code để lấy token từ Google
            String accessToken = googleAuthService.getAccessTokenFromCode(code);

            // Sử dụng access token để lấy thông tin người dùng
            Map<String, Object> userInfo = googleAuthService.getUserInfoFromAccessToken(accessToken);
            System.out.println(userInfo);
            String email = (String) userInfo.get("email");
            String googleId = (String) userInfo.get("sub");

            if (email == null || googleId == null) {
                return ResponseEntity.badRequest().body("Không thể lấy thông tin từ Google");
            }

            Account account = accountRepository.findByGoogleId(googleId)
                    .orElse(accountRepository.findByEmail(email).orElse(null));

            if (account != null) {
                if (account.getIsDeleted()) {
                    URI errorUri = UriComponentsBuilder.fromUriString("http://localhost:5173/google-auth-callback")
                            .queryParam("error", "Tài khoản này đã bị vô hiệu hóa.")
                            .build().toUri();
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(errorUri)
                            .build();
                }
            } else {
                account = Account.builder()
                        .email(email)
                        .googleId(googleId)
                        .role("CUSTOMER")
                        .avatar((String) userInfo.get("picture"))
                        .isDeleted(false)
                        .build();
                account = accountService.createAccount(account);

                Customer customer = Customer.builder()
                        .account(account)
                        .fullName((String) userInfo.get("name"))
                        .build();
                customerService.createCustomer(customer);
            }

            String jwtToken = jwtService.generateToken(email, account.getRole());
            String refreshToken = jwtService.generateRefreshToken(email, account.getRole());

            account.setRefreshToken(refreshToken);
            accountRepository.save(account);

            URI redirectUri = UriComponentsBuilder.fromUriString("http://localhost:5173/google-auth-callback")
                    .queryParam("accessToken", jwtToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUri();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();
        } catch (Exception e) {
            URI errorUri = UriComponentsBuilder.fromUriString("http://localhost:5173/google-auth-callback")
                    .queryParam("error", e.getMessage())
                    .build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(errorUri)
                    .build();
        }
    }

    @GetMapping("/facebook")
    public ResponseEntity<?> facebookAuth() {
        String authUrl = facebookAuthService.createAuthorizationURL();
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }

    @GetMapping("/facebook/callback")
    @Transactional
    public ResponseEntity<?> facebookCallback(@RequestParam("code") String code) {
        try {
            // Sử dụng code để lấy token từ Facebook
            String accessToken = facebookAuthService.getAccessTokenFromCode(code);

            // Sử dụng access token để lấy thông tin người dùng
            Map<String, Object> userInfo = facebookAuthService.getUserInfoFromAccessToken(accessToken);
            System.out.println(userInfo);
            String email = (String) userInfo.get("email");
            String facebookId = (String) userInfo.get("id");

            if (email == null || facebookId == null) {
                return ResponseEntity.badRequest().body("Không thể lấy thông tin từ Facebook");
            }

            Account account = accountRepository.findByFacebookId(facebookId)
                    .orElse(accountRepository.findByEmail(email).orElse(null));

            if (account != null) {
                if (account.getIsDeleted()) {
                    URI errorUri = UriComponentsBuilder.fromUriString("http://localhost:5173/facebook-auth-callback")
                            .queryParam("error", "Tài khoản này đã bị vô hiệu hóa.")
                            .build().toUri();
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(errorUri)
                            .build();
                }
            } else {
                account = Account.builder()
                        .email(email)
                        .facebookId(facebookId)
                        .role("CUSTOMER")
                        .avatar((String) userInfo.get("picture.data.url"))
                        .isDeleted(false)
                        .build();
                account = accountService.createAccount(account);

                // Tạo hồ sơ khách hàng mới
                Customer customer = Customer.builder()
                        .account(account)
                        .fullName((String) userInfo.get("name"))
                        .build();
                customerService.createCustomer(customer);
            }

            String jwtToken = jwtService.generateToken(email, account.getRole());
            String refreshToken = jwtService.generateRefreshToken(email, account.getRole());

            account.setRefreshToken(refreshToken);
            accountRepository.save(account);

            URI redirectUri = UriComponentsBuilder.fromUriString("http://localhost:5173/facebook-auth-callback")
                    .queryParam("accessToken", jwtToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUri();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();
        } catch (Exception e) {
            URI errorUri = UriComponentsBuilder.fromUriString("http://localhost:5173/facebook-auth-callback")
                    .queryParam("error", e.getMessage())
                    .build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(errorUri)
                    .build();
        }
    }

    @PutMapping("/update-Image")
    public ResponseEntity<?> updateAccount(@RequestBody AccountUpdateRequest updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            Account account = accountRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

            if (updateRequest.getAvatar() != null && !updateRequest.getAvatar().isEmpty()) {
                account.setAvatar(updateRequest.getAvatar());
            }

            Account updatedAccount = accountRepository.save(account);

            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cập nhật ảnh đại diện thất bại: " + e.getMessage());
        }
    }
}