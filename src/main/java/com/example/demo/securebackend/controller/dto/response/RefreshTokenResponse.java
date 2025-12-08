package com.example.demo.securebackend.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenResponse {
    String refreshToken;
    String jwtToken;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    LocalDateTime jwtTokenTimeout;
}
