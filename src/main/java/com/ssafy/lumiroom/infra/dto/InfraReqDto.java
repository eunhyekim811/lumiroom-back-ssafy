package com.ssafy.lumiroom.infra.dto;

public record InfraReqDto (
        String type,      // cctv, securityLight, streetLight
        double swLat,     // 남서쪽 위도
        double swLng,     // 남서쪽 경도
        double neLat,     // 북동쪽 위도
        double neLng      // 북동쪽 경도
) {}