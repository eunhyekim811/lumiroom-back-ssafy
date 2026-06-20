package com.ssafy.lumiroom.batch.controller;

import com.ssafy.lumiroom.batch.mapper.PropertyMapper;
import com.ssafy.lumiroom.batch.service.PropertyGeocodingService;
import com.ssafy.lumiroom.batch.service.PropertyGeocodingService.GeocodingResult;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PropertyBatchController {

    private final PropertyMapper propertyMapper;
    private final PropertyGeocodingService propertyGeocodingService;

    @GetMapping("/batch/properties")
    @Transactional
    public String upsertProperties() {
        int affectedRows = propertyMapper.upsertPropertiesFromRealEstateTrades();
        return "실거래가 기반 매물 집계 적재 완료: " + affectedRows + " rows affected";
    }

    @GetMapping("/batch/properties/geocode")
    public String geocodeProperties(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "1") int concurrency
    ) {
        GeocodingResult result = propertyGeocodingService.geocodeMissingProperties(limit, concurrency);
        String message = "매물 좌표 지오코딩 완료: 대상 " + result.targetCount()
                + "건, 성공 " + result.successCount()
                + "건, 실패 " + result.failCount() + "건";
        if (!result.failureSamples().isEmpty()) {
            message += ", 실패 샘플 " + result.failureSamples();
        }
        return message;
    }
}
