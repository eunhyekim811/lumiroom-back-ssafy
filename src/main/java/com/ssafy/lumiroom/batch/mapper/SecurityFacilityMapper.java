package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.SecurityFacilityInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SecurityFacilityMapper {
    void upsertSecurityFacility(SecurityFacilityInfo info);
}
