package com.ssafy.lumiroom.domain.model.mapper;

import com.ssafy.lumiroom.domain.model.dto.InfraResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InfraMapper {
    List<InfraResponse> selectCctvs(@Param("lat") double lat,
                                    @Param("lng") double lng,
                                    @Param("radius") double radius);

    List<InfraResponse> selectSecurityLights(@Param("lat") double lat,
                                             @Param("lng") double lng,
                                             @Param("radius") double radius);

    List<InfraResponse> selectStreetLights(@Param("lat") double lat,
                                           @Param("lng") double lng,
                                           @Param("radius") double radius);

    List<InfraResponse> selectPoliceFacilities(@Param("lat") double lat,
                                               @Param("lng") double lng,
                                               @Param("radius") double radius);
}
