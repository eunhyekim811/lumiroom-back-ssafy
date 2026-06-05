package com.ssafy.lumiroom.batch.processor;

import com.ssafy.lumiroom.batch.domain.CctvInfo;
import com.ssafy.lumiroom.batch.dto.CctvDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class CctvItemProcessor implements ItemProcessor<CctvDto, CctvInfo> {

    @Override
    public CctvInfo process(CctvDto dto) throws Exception {
        double lat = parseDouble(dto.getLat());
        double lon = parseDouble(dto.getLon());

        // 좌표가 없는 지저분한 데이터는 버림(null 반환 시 Writer로 넘어가지 않음)
        if (lat == 0.0 || lon == 0.0) {
            return null;
        }

        // 위도는 -90 ~ 90, 경도는 -180 ~ 180 사이
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            log.warn("비정상 좌표 데이터 발견 및 스킵 - 관리번호: {}, 입력된 위도: {}, 경도: {}", dto.getManagementNo(), lat, lon);
            // null을 반환하면 스프링 배치가 이 데이터만 DB에 넣지 않고 조용히 다음 줄로 넘어갑니다!
            return null;
        }

        // ★ POINT 생성 시 파라미터 순서는 반드시 (위도 경도) 순서로 공백 구분입니다.
        String wkt = String.format("POINT(%f %f)", lat, lon);

        return CctvInfo.builder()
                .managementNo(dto.getManagementNo())
                .openGovCode(dto.getOpenGovCode())
                .agencyName(dto.getAgencyName())
                .roadAddress(dto.getRoadAddress())
                .lotAddress(dto.getLotAddress())
                .purpose(dto.getPurpose())
                .cameraCount(parseInteger(dto.getCameraCount()))
                .pixels(parseInteger(dto.getPixels()))
                .direction(dto.getDirection())
                .storageDays(parseInteger(dto.getStorageDays()))
                .installYm(dto.getInstallYm())
                .phone(dto.getPhone())
                .baseDate(dto.getBaseDate())
                .locationWkt(wkt)
                .build();
    }

    private Integer parseInteger(String str) {
        if (!StringUtils.hasText(str) || "nan".equalsIgnoreCase(str)) return 0;
        try { return Integer.parseInt(str.trim()); } catch (NumberFormatException e) { return 0; }
    }

    private Double parseDouble(String str) {
        if (!StringUtils.hasText(str) || "nan".equalsIgnoreCase(str)) return 0.0;
        try { return Double.parseDouble(str.trim()); } catch (NumberFormatException e) { return 0.0; }
    }
}