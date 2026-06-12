package com.ssafy.lumiroom.users.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private String nickname;
    private String role; // 'USER', 'ADMIN'
    private LocalDateTime createdAt;
}