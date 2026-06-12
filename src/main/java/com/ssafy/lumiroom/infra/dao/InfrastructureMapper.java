package com.ssafy.lumiroom.infra.dao;

import com.ssafy.lumiroom.infra.dto.InfraReqDto;
import com.ssafy.lumiroom.infra.dto.InfraResDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InfrastructureMapper {
    List<InfraResDto> getInfraMarkersByBounds(InfraReqDto dto);
}