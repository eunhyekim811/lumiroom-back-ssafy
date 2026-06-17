package com.ssafy.lumiroom.batch.controller;

import com.ssafy.lumiroom.batch.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PropertyBatchController {

    private final PropertyMapper propertyMapper;

    @GetMapping("/batch/properties")
    @Transactional
    public String upsertProperties() {
        int affectedRows = propertyMapper.upsertPropertiesFromRealEstateTrades();
        return "실거래가 기반 매물 집계 적재 완료: " + affectedRows + " rows affected";
    }
}
