package com.ssafy.lumiroom.batch.mapper;

import com.ssafy.lumiroom.batch.domain.LegalDongInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LegalDongMapper {
    void upsertLegalDong(LegalDongInfo legalDongInfo);
}
