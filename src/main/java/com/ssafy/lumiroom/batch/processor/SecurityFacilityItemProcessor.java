package com.ssafy.lumiroom.batch.processor;

import com.ssafy.lumiroom.batch.domain.SecurityFacilityInfo;
import com.ssafy.lumiroom.batch.dto.SecurityFacilityDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SecurityFacilityItemProcessor implements ItemProcessor<SecurityFacilityDto, SecurityFacilityInfo> {

    @Override
    public SecurityFacilityInfo process(SecurityFacilityDto item) {
        if (!StringUtils.hasText(item.getObjtId())) {
            return null;
        }

        return SecurityFacilityInfo.builder()
                .objtId(item.getObjtId())
                .police(item.getPolice())
                .polcsttn(item.getPolcsttn())
                .fcltyTy(item.getFcltyTy())
                .fcltyCd(item.getFcltyCd())
                .fcltyNm(item.getFcltyNm())
                .adres(item.getAdres())
                .rnAdres(item.getRnAdres())
                .telno(item.getTelno())
                .ctprvnCd(item.getCtprvnCd())
                .sggCd(item.getSggCd())
                .x(item.getX())
                .y(item.getY())
                .tmpX(item.getTmpX())
                .tmpY(item.getTmpY())
                .build();
    }
}
