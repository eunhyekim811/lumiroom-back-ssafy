package com.ssafy.lumiroom.review.dto;

public record ReviewReq(
        Long propertyId,
        String content,
        Integer rating
) {}