package com.ssafy.lumiroom.ai.service;

import com.ssafy.lumiroom.ai.dao.AiInfraMapper;
import com.ssafy.lumiroom.ai.dto.InfraStatsDto;
import com.ssafy.lumiroom.ai.dto.LocationReqDto;
import com.ssafy.lumiroom.ai.dto.StructuredBriefing;
import com.ssafy.lumiroom.ai.dao.AiInfraMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SafetyBriefingServiceImpl implements  SafetyBriefingService{

    private final ChatClient chatClient;
    private final AiInfraMapper aiInfraMapper;

    public SafetyBriefingServiceImpl(ChatClient.Builder chatClientBuilder, AiInfraMapper aiInfraMapper) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        너는 대한민국 최고의 지능형 도시 치안인프라 빅데이터 분석 전문가야.
                        제공된 가로등, 보안등, CCTV 수량 및 인근 치안시설(지구대, 파출소, 경찰서 등) 통계를 기반으로
                        안전 진단을 내려야 해. 오직 제시된 'StructuredBriefing' JSON 스키마 구조에 100% 호환되는 객체 데이터를 응답하도록 해줘.
                        답변 문자열 외부에는 마크다운 기호(```json 등)를 절대로 추가하지 마라.
                        """)
                .build();
        this.aiInfraMapper = aiInfraMapper;
    }

    public StructuredBriefing generateStructuredBriefing(LocationReqDto reqDto) {
        // [1. EPSG:4326 -> EPSG:3857 좌표 투영 수식 적용]
        // 지구 타원체의 적도 반경상 미터 단위 환산 공식 사용
        double rMajor = 6378137.0;
        double epsg3857X = Math.toRadians(reqDto.lon()) * rMajor;
        double epsg3857Y = Math.log(Math.tan(Math.PI / 4.0 + Math.toRadians(reqDto.lat()) / 2.0)) * rMajor;

        // 위도에 따른 메르카토르 도법 왜곡 비율(Scale Factor) 보정 계산
        double cosLat = Math.cos(Math.toRadians(reqDto.lat()));
        double projectedRadius = 1000.0 / cosLat; // 1000m 실거리를 도법 미터단위 구역 크기로 스케일 업

        // [2. Retrieval 단계]
        String wktPoint = String.format("POINT(%f %f)", reqDto.lat(), reqDto.lon());
        InfraStatsDto stats = aiInfraMapper.getSafetyInfraStats(
                wktPoint, 1000, epsg3857X, epsg3857Y, projectedRadius
        );

        // [3. Augment 단계]
        String promptTemplate = """
           분석 구역 중심 좌표: 위도 {lat}, 경도 {lon} 인근 지역
            
           해당 위치 반경 1000m 인프라 수집 데이터:
           - 방범용 CCTV: {cctv}대
           - 도로 가로등: {streetLight}대
           - 안심 보안등: {securityLight}대
           - 치안안전시설(파출소/지구대/경찰서): {policeStation}대
            
           [미션 및 출력 스키마 매핑 가이드]
           위 데이터를 종합해 해당 좌표 구역의 안전 점수(0~100점) 및 등급(A~D)을 산정하고 상세 리포트 객체를 조립해줘.
           인근에 관련 치안 인프라(cctv/보안등/가로등/치안안전시설) 수량이 다양하고 풍부할 경우, 심리적 및 실질적 밤길 도보 안전감이 크다는 점을 강점으로 부각시켜줘.
           특히, 치안안전시설의 수량이 1개 이상일 경우 112 긴급 조치 기동성이 매우 탁월하다는 점을 강조해서 서술해줘.
           한 줄 요약이나 총평 내에서도 굳이 행정 지명을 언급하려 하지 말고, "선택하신 좌표 구역은..." 이나 "해당 반경 구역은..." 과 같은 정형화된 공간 분석 양식을 사용해줘.
           
           최종 출력 JSON의 아래 필드에는 가이드에 적힌 인프라 수치 데이터를 왜곡 없이 숫자 그대로 매핑하여 포함시켜서 채워줘:
           - cctvCount: {cctv}
           - streetLightCount: {streetLight}
           - securityLightCount: {securityLight}
           - policeStationCount: {policeStation}
           """;

        System.out.println("cctv(" + stats.cctvCount() + "), 보안등(" + stats.securityLightCount() + "), 가로등("
            + stats.streetLightCount() + "), 치안안전시설(" + stats.policeStationCount() + ")");

        // [4. Generation 단계] Spring AI Structured Output 인터페이스 사용
        return chatClient.prompt()
                .user(u -> u.text(promptTemplate)
                        .param("lat", reqDto.lat())
                        .param("lon", reqDto.lon())
                        .param("cctv", stats.cctvCount())
                        .param("streetLight", stats.streetLightCount())
                        .param("securityLight", stats.securityLightCount())
                        .param("policeStation", stats.policeStationCount())
                )
                .call()
                .entity(StructuredBriefing.class);
    }
}