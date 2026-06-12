package com.ssafy.lumiroom.review.dto;

import java.time.LocalDateTime;

public record ReviewRes(
        Long id,
        Long userId,
        String userEmail, // 작성자 아이디(이메일) 표시용
        Long propertyId,
        String content,
        Integer rating,
        LocalDateTime createdAt
) {}