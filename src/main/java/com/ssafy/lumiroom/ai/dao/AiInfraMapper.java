package com.ssafy.lumiroom.ai.dao;

import com.ssafy.lumiroom.ai.dto.InfraStatsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiInfraMapper {
    // WKT 형태의 좌표("POINT(경도 위도)")를 받아 반경 내 개수를 조회
    InfraStatsDto getSafetyInfraStats(@Param("wktPoint") String wktPoint, @Param("radius") int radius);
}