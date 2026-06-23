package com.ssafy.lumiroom.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PropertySafetyCounts {

    private Long propertyId;
    private int cctvCount;
    private int securityLightCount;
    private int securityFacilityCount;
}
