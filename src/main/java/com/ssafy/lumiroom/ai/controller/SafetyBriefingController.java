package com.ssafy.lumiroom.ai.controller;

import com.ssafy.lumiroom.ai.dto.LocationReqDto;
import com.ssafy.lumiroom.ai.service.SafetyBriefingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/briefing")
@RequiredArgsConstructor
public class SafetyBriefingController {

    private final SafetyBriefingService safetyBriefingService;

    /**
     * 프론트엔드 예시 호출: GET /api/ai/briefing?lat=37.5665&lon=126.9780&regionName=서울시청
     */
    @GetMapping
    public String getSafetyBriefing(@ModelAttribute LocationReqDto locationReqDto) {
        return safetyBriefingService.generateBriefing(locationReqDto);
    }
}