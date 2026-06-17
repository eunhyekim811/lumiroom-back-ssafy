package com.ssafy.lumiroom.batch.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PropertyMapper {
    int upsertPropertiesFromRealEstateTrades();
}
