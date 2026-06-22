package com.ssafy.lumiroom.ai.service;

import com.ssafy.lumiroom.ai.dto.LocationReqDto;
import com.ssafy.lumiroom.ai.dto.StructuredBriefing;

public interface SafetyBriefingService {
    public StructuredBriefing generateStructuredBriefing(LocationReqDto reqDto);
}
