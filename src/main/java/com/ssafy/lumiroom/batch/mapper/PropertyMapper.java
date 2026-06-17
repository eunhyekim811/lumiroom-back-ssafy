package com.ssafy.lumiroom.batch.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import com.ssafy.lumiroom.batch.domain.PropertyGeocodeTarget;

@Mapper
public interface PropertyMapper {
    int upsertPropertiesFromRealEstateTrades();

    List<PropertyGeocodeTarget> findPropertiesMissingCoordinates(@Param("limit") int limit);

    int updatePropertyCoordinates(
            @Param("id") Long id,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude
    );
}
