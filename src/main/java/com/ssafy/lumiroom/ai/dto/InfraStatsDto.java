package com.ssafy.lumiroom.ai.dto;

public record InfraStatsDto(
        int cctvCount,
        int securityLightCount,
        int policeStationCount, 
        int streetLightCount
) {}