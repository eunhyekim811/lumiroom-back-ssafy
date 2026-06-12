package com.ssafy.lumiroom.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PropertyReview {
    private Long id;
    private Long userId;
    private Long propertyId;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}