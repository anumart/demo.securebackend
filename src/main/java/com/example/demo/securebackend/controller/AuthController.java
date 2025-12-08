package com.example.demo.securebackend.controller;

import com.example.demo.securebackend.constant.AppConstants;
import com.example.demo.securebackend.controller.dto.request.AuthRequest;
import com.example.demo.securebackend.controller.dto.request.RefreshTokenRequest;
import com.example.demo.securebackend.controller.dto.request.RegisterRequest;
import com.example.demo.securebackend.controller.dto.response.AuthResponse;
import com.example.demo.securebackend.controller.dto.response.RefreshTokenResponse;
import com.example.demo.securebackend.exception.InvalidCredentialsException;
import com.example.demo.securebackend.repository.entity.User;
import com.example.demo.securebackend.security.JwtService;
import com.example.demo.securebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller สำหรับจัดการเส้นทาง /api/v1/auth (Authentication)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
//    private final UserRepository userRepository;
    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * POST /api/v1/auth/register : การลงทะเบียนผู้ใช้ใหม่
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());
        if (userService.getUserByUsername(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AppConstants.Role.valueOf(request.getRole()))
                .active(true)
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();

        // สร้าง Token ทันทีหลังลงทะเบียน
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userService.createUser(user);

        user = userService.getUserByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));

        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .me(user)
                .build());
    }

    /**
     * POST /api/v1/auth/login : การเข้าสู่ระบบ
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());
        try {
            // Spring Security จะตรวจสอบ Username/Password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            log.warn("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // หากตรวจสอบผ่าน
        User user = userService.getUserByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));

        // สร้าง Access Token และ Refresh Token ใหม่
        String jwtToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // อัพเดท Refresh Token ใน DB
        user.setRefreshToken(newRefreshToken);
        userService.updateRefreshToken(user);

        log.info("User authenticated successfully: {}", request.getUsername());
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(newRefreshToken)
                .me(user)
                .build());
    }

    /**
     * POST /api/v1/auth/refresh : การใช้ Refresh Token เพื่อขอ Access Token ใหม่
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Attempting to refresh token.");
        final String username;

        try {
            username = jwtService.extractUsername(request.getToken());
        } catch (Exception e) {
            log.error("Invalid Refresh Token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<User> userOptional = userService.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 1. ตรวจสอบว่า Refresh Token ที่ให้มาตรงกับที่เก็บใน DB และยังไม่หมดอายุ
            if (user.getRefreshToken() != null && user.getRefreshToken().equals(request.getToken()) && jwtService.isTokenValid(request.getToken(), user)) {

                // 2. สร้าง Access Token ใหม่
                String newAccessToken = jwtService.generateToken(user);

                // 3. สร้าง Refresh Token ใหม่ (เพื่อหมุนเวียน Token: Refresh Token Rotation)
                String newRefreshToken = jwtService.generateRefreshToken(user);

                // 4. อัพเดท Refresh Token ใน DB
                user.setRefreshToken(newRefreshToken);
                userService.updateRefreshToken(user);

                log.info("Token refreshed successfully for user: {}", username);

                return ResponseEntity.ok(RefreshTokenResponse.builder()
                        .jwtToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build());
            }
        }
        log.warn("Refresh Token validation failed or user not found.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
