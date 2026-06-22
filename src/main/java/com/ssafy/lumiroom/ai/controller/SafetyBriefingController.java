package com.ssafy.lumiroom.ai.controller;

import com.ssafy.lumiroom.ai.dto.LocationReqDto;
import com.ssafy.lumiroom.ai.dto.StructuredBriefing;
import com.ssafy.lumiroom.ai.service.SafetyBriefingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/briefing")
@RequiredArgsConstructor
public class SafetyBriefingController {

    private final SafetyBriefingService safetyBriefingService;

    @GetMapping
    public ResponseEntity<StructuredBriefing> getSafetyBriefing(@ModelAttribute LocationReqDto locationReqDto) {
        StructuredBriefing response = safetyBriefingService.generateStructuredBriefing(locationReqDto);
        return ResponseEntity.ok(response);
    }
}