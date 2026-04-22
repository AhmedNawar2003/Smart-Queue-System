package com.smartqueue.auth_service.service;

import com.smartqueue.auth_service.client.UserServiceClient;
import com.smartqueue.auth_service.dto.AdminRegisterRequest;
import com.smartqueue.auth_service.dto.AuthResponse;
import com.smartqueue.auth_service.dto.LoginRequest;
import com.smartqueue.auth_service.dto.RegisterRequest;
import com.smartqueue.auth_service.model.Role;
import com.smartqueue.auth_service.model.User;
import com.smartqueue.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final  UserServiceClient userServiceClient;

    @Value("${app.admin-secret}")
    private String adminSecret;

    public AuthResponse registerWithRole(AdminRegisterRequest request) {

        // تحقق من الـ secret
        if (!request.getAdminSecret().equals(adminSecret)) {
            throw new IllegalArgumentException("Invalid admin secret");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())   // أي role
                .build();

        userRepository.save(user);
        log.info("New {} registered: {}", request.getRole(), user.getEmail());

        // أنشئ Profile في User Service
        try {
            userServiceClient.createProfile(
                    new UserServiceClient.CreateProfileRequest(
                            user.getId().toString(),
                            user.getEmail(),
                            user.getFullName(),
                            user.getPhone()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to create profile: {}", e.getMessage());
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .role(user.getRole().name())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }


    // ── Register ─────────────────────────────────────────────

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 1. احفظ في auth_db
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // 2. أنشئ Profile في user_db عبر User Service
        try {
            userServiceClient.createProfile(
                    new UserServiceClient.CreateProfileRequest(
                            user.getId().toString(),
                            user.getEmail(),
                            user.getFullName(),
                            user.getPhone()
                    )
            );
            log.info("Profile created in User Service for: {}", user.getEmail());
        } catch (Exception e) {
            // مش هنفشل الـ register لو User Service مش شغال
            log.error("Failed to create profile in User Service: {}", e.getMessage());
        }

        // 3. ارجع الـ JWT
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .role(user.getRole().name())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    // ── Login ────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {

        // Spring Security بيتحقق من الـ credentials أوتوماتيك
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .role(user.getRole().name())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
