package com.ssafy.lumiroom.ai.service;

import com.ssafy.lumiroom.ai.dto.LocationReqDto;

public interface SafetyBriefingService {
    public String generateBriefing(LocationReqDto reqDto);
}
