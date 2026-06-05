package com.ssafy.lumiroom.batch.processor;

import com.ssafy.lumiroom.batch.domain.SecurityLightInfo;
import com.ssafy.lumiroom.batch.dto.SecurityLightDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SecurityLightItemProcessor implements ItemProcessor<SecurityLightDto, SecurityLightInfo> {

    @Override
    public SecurityLightInfo process(SecurityLightDto item) throws Exception {
//        System.out.println(">>> [Processor 수신 데이터] SN: " + item.getSn() + ", X: " + item.getXmapCrts());
        // 비어있는 좌표 방어 로직
        if (item.getXmapCrts() == null || item.getYmapCrts() == null) {
            return null; // 좌표가 없으면 스킵
        }

        double x = item.getXmapCrts();
        double y = item.getYmapCrts();

        // 경도
        double lon = (x * 180.0) / 20037508.34;
        // 위도
        double lat = Math.atan(Math.exp(y * Math.PI / 20037508.34)) * 360.0 / Math.PI - 90.0;

        // WKT 문자열 생성. X가 경도/East, Y가 위도/North 역할
        String locationWkt = String.format("POINT(%f %f)", lat, lon);

        return SecurityLightInfo.builder()
                .sn(item.getSn())
                .fcltType(item.getFcltType())
                .fcltCd(item.getFcltCd())
                .fcltGvmnfcNm(item.getFcltGvmnfcNm())
                .addr(item.getAddr())
                .roadNmAddr(item.getRoadNmAddr())
                .stdgCtpvCd(item.getStdgCtpvCd())
                .stdgSggCd(item.getStdgSggCd())
                .stdgEmdCd(item.getStdgEmdCd())
                .locationWkt(locationWkt)
                .build();
    }
}