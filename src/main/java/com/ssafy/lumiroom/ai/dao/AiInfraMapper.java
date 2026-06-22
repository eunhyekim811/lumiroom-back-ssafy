package com.ssafy.lumiroom.ai.dao;

import com.ssafy.lumiroom.ai.dto.InfraStatsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiInfraMapper {
    InfraStatsDto getSafetyInfraStats(
            @Param("wktPoint") String wktPoint,
            @Param("radius") int radius,
            @Param("epsg3857X") double epsg3857X,
            @Param("epsg3857Y") double epsg3857Y,
            @Param("projectedRadius") double projectedRadius
    );
}