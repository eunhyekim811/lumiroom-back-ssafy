package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.PropertySafetySummary;
import com.ssafy.lumiroom.batch.dto.PropertySafetyCounts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PropertySafetySummaryMapper {

    PropertySafetyCounts countSafetyFacilities(
            @Param("propertyId") Long propertyId,
            @Param("radiusM") int radiusM
    );

    int upsertPropertySafetySummary(PropertySafetySummary summary);
}
