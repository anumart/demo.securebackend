package com.example.demo.securebackend.config;

import com.example.demo.securebackend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * กำหนดค่าความปลอดภัยหลักของ Spring Security
 * 1. กำหนดเส้นทางที่อนุญาตให้เข้าถึงได้โดยไม่ต้องตรวจสอบสิทธิ์ (Whitelist)
 * 2. กำหนดให้ใช้ Session เป็น Stateless
 * 3. เพิ่ม JwtAuthenticationFilter ก่อน UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // ปิด CSRF เนื่องจากใช้ JWT (Stateless)
                .authorizeHttpRequests(auth -> auth
                        // อนุญาตให้เข้าถึง API การตรวจสอบสิทธิ์โดยตรง
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // เส้นทางอื่นทั้งหมดต้องมีการตรวจสอบสิทธิ์
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                // เพิ่ม JWT Filter ก่อนการตรวจสอบสิทธิ์ username/password มาตรฐาน
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
