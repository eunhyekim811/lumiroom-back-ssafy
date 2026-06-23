package com.ssafy.lumiroom.batch.processor;

import com.ssafy.lumiroom.batch.domain.PropertySafetySummary;
import com.ssafy.lumiroom.batch.dto.PropertySafetyCounts;
import com.ssafy.lumiroom.batch.mapper.PropertySafetySummaryMapper;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class PropertySafetySummaryProcessor implements ItemProcessor<Long, PropertySafetySummary> {

    private final PropertySafetySummaryMapper mapper;
    private final int radiusM;
    private final String calculationVersion;
    private final int cctvTargetCount;
    private final int securityLightTargetCount;
    private final int securityFacilityTargetCount;
    private final BigDecimal cctvWeight;
    private final BigDecimal securityLightWeight;
    private final BigDecimal securityFacilityWeight;

    public PropertySafetySummaryProcessor(
            PropertySafetySummaryMapper mapper,
            int radiusM,
            String calculationVersion,
            int cctvTargetCount,
            int securityLightTargetCount,
            int securityFacilityTargetCount,
            BigDecimal cctvWeight,
            BigDecimal securityLightWeight,
            BigDecimal securityFacilityWeight
    ) {
        this.mapper = mapper;
        this.radiusM = radiusM;
        this.calculationVersion = calculationVersion;
        this.cctvTargetCount = cctvTargetCount;
        this.securityLightTargetCount = securityLightTargetCount;
        this.securityFacilityTargetCount = securityFacilityTargetCount;
        this.cctvWeight = cctvWeight;
        this.securityLightWeight = securityLightWeight;
        this.securityFacilityWeight = securityFacilityWeight;
    }

    @Override
    public PropertySafetySummary process(Long propertyId) {
        PropertySafetyCounts counts = mapper.countSafetyFacilities(propertyId, radiusM);
        BigDecimal score = weightedScore(counts);

        return PropertySafetySummary.builder()
                .propertyId(propertyId)
                .safetyScore(score)
                .safetyGrade(toGrade(score))
                .cctvCount(counts.getCctvCount())
                .securityLightCount(counts.getSecurityLightCount())
                .securityFacilityCount(counts.getSecurityFacilityCount())
                .radiusM(radiusM)
                .calculationVersion(calculationVersion)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    private BigDecimal weightedScore(PropertySafetyCounts counts) {
        BigDecimal score = contribution(counts.getCctvCount(), cctvTargetCount, cctvWeight)
                .add(contribution(counts.getSecurityLightCount(), securityLightTargetCount, securityLightWeight))
                .add(contribution(counts.getSecurityFacilityCount(), securityFacilityTargetCount, securityFacilityWeight));
        return score.min(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal contribution(int count, int targetCount, BigDecimal weight) {
        if (targetCount <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ratio = BigDecimal.valueOf(count)
                .divide(BigDecimal.valueOf(targetCount), 6, RoundingMode.HALF_UP)
                .min(BigDecimal.ONE);
        return ratio.multiply(weight);
    }

    private String toGrade(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "A";
        }
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "B";
        }
        if (score.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "C";
        }
        return "D";
    }
}
