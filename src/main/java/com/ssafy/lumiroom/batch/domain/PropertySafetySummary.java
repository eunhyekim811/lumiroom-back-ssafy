package com.ssafy.lumiroom.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySafetySummary {

    private Long propertyId;
    private BigDecimal safetyScore;
    private String safetyGrade;
    private int cctvCount;
    private int securityLightCount;
    private int securityFacilityCount;
    private int radiusM;
    private String calculationVersion;
    private LocalDateTime calculatedAt;
}
