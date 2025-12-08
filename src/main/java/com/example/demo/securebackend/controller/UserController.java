package com.example.demo.securebackend.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller ตัวอย่างสำหรับ API ที่มีการป้องกัน
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * GET /api/v1/user/profile : เส้นทางที่ต้องการ JWT Token ใน Header
     */
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        // ดึงข้อมูลผู้ใช้จาก SecurityContextHolder
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Accessing profile for user: {}", email);
        return ResponseEntity.ok("Welcome to your secure profile, " + email + "! This content is protected by JWT.");
    }
}
