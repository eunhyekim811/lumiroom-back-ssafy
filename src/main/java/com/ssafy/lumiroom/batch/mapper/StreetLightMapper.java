package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.StreetLightInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StreetLightMapper {
    void upsertStreetLights(@Param("list") List<StreetLightInfo> list);
}