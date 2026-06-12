package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.RealEstateTradeInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RealEstateTradeMapper {
    void upsertRealEstateTrade(RealEstateTradeInfo realEstateTradeInfo);
}
