package com.ssafy.lumiroom.ai.dto;

public record InfraStatsDto(
        int cctvCount,
        int streetLightCount,
        int securityLightCount
) {}