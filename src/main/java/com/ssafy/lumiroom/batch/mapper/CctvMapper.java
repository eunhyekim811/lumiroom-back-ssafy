package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.CctvInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CctvMapper {
    void upsertCctv(CctvInfo cctvInfo);
}