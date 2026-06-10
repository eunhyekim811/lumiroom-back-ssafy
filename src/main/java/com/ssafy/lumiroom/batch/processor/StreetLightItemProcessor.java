package com.ssafy.lumiroom.batch.processor;

import com.ssafy.lumiroom.batch.domain.StreetLightInfo;
import com.ssafy.lumiroom.batch.dto.StreetLightDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StreetLightItemProcessor implements ItemProcessor<StreetLightDto, StreetLightInfo> {

    @Override
    public StreetLightInfo process(StreetLightDto item) throws Exception {
        // 필수 좌표나 일련번호가 누락된 데이터는 버림(스킵)
        if (item.getSn() == null || item.getXmapCrts() == null || item.getYmapCrts() == null) {
            return null;
        }

        // EPSG:3857(Web Mercator) -> EPSG:4326(WGS84 위경도) 변환 공식
        double lon = (item.getXmapCrts() / 20037508.34) * 180.0;
        double lat = (item.getYmapCrts() / 20037508.34) * 180.0;
        lat = 180.0 / Math.PI * (2.0 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);

        String locationWkt = String.format("POINT(%.7f %.7f)", lat, lon);

        return StreetLightInfo.builder()
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