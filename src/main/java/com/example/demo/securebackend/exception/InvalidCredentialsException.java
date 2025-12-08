package com.example.demo.securebackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception สำหรับเมื่อข้อมูลการตรวจสอบสิทธิ์ไม่ถูกต้อง (Invalid username/password)
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // ส่งคืน HTTP 401
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}