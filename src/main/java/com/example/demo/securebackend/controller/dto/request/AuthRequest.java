package com.example.demo.securebackend.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO สำหรับการเข้าสู่ระบบ (Login)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
//    @Email(message = "Email must be valid")
//    @NotBlank(message = "Username is required")
    private String username;

//    @NotBlank(message = "Password is required")
    private String password;
}
