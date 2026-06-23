package com.ssafy.lumiroom.ai.dao;

import com.ssafy.lumiroom.ai.dto.InfraStatsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AiInfraMapper {
    InfraStatsDto getSafetyInfraStats(
            @Param("wktPoint") String wktPoint,
            @Param("radius") int radius,
            @Param("epsg3857X") double epsg3857X,
            @Param("epsg3857Y") double epsg3857Y,
            @Param("projectedRadius") double projectedRadius
    );
    
    InfraStatsDto getCombinedSafetyStats(
            @Param("propertyId") Long propertyId,
            @Param("wktPoint") String wktPoint,
            @Param("radius") int radius
    );

    @Select("SELECT region_name FROM properties WHERE id = #{propertyId}")
    String getRegionNameByPropertyId(@Param("propertyId") Long propertyId);
}