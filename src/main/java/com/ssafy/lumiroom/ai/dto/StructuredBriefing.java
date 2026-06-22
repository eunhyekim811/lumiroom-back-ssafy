package com.ssafy.lumiroom.ai.dto;

import java.util.List;

public record StructuredBriefing(
        String safetyGrade,             // 안전 등급 (A, B, C, D)
        int safetyScore,                // 종합 안전 점수 (0 ~ 100)
        String oneLineSummary,          // 이 동네 치안 한 줄 요약
        List<String> positivePoints,    // 안전 인프라의 주요 장점 리스트
        List<String> warningPoints,     // 주의해야 할 밤길 취약 구역 및 우려 사항 리스트
        String nightWalkingAdvice,      // 심야 안심 도보 팁 및 가이드
        String totalReview,             // 안전 분석가 시점의 총평

        // 정량적 치안 인프라 수치 필드 추가
        int cctvCount,                  // 반경 내 방범 CCTV 개수
        int streetLightCount,           // 반경 내 가로등 개수
        int securityLightCount,         // 반경 내 보안등 개수
        int policeStationCount          // 반경 내 치안안전시설(파출소/지구대/경찰서) 개수
) {}