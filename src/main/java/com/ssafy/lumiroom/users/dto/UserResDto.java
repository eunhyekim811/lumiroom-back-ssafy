package com.ssafy.lumiroom.users.dto;

import java.time.LocalDateTime;

public record UserResDto(
        String email,
        String name,
        String role,
        LocalDateTime createdAt
) {}