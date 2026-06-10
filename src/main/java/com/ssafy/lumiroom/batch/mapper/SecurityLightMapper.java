package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.SecurityLightInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SecurityLightMapper {
    void upsertSecurityLight(SecurityLightInfo info);
}