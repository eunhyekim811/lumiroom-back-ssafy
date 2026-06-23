package com.ssafy.lumiroom.domain.model.mapper;

import com.ssafy.lumiroom.domain.model.dto.PropertyListItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyListMapper {

    List<PropertyListItemResponse> selectPropertiesWithinRadius(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("limit") int limit,
            @Param("offset") long offset
    );

    long countPropertiesWithinRadius(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );
    
    PropertyListItemResponse findByIdWithSafety(@Param("id") Long id);
    
}
