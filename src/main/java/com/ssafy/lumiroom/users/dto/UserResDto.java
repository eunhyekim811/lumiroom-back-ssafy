package com.ssafy.lumiroom.users.dto;

import java.time.LocalDateTime;

public record UserResDto(
        Long id,
        String email,
        String name,
        String role,
        LocalDateTime createdAt
) {}