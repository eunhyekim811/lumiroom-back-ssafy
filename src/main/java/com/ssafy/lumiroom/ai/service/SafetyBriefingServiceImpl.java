package com.ssafy.lumiroom.ai.service;

import com.ssafy.lumiroom.ai.config.WebSearchToolConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.ssafy.lumiroom.ai.dao.AiInfraMapper;
import com.ssafy.lumiroom.ai.dto.InfraStatsDto;
import com.ssafy.lumiroom.ai.dto.LocationReqDto;
import com.ssafy.lumiroom.ai.dto.StructuredBriefing;

@Service
public class SafetyBriefingServiceImpl implements  SafetyBriefingService{

    private final ChatClient chatClient;
    private final AiInfraMapper aiInfraMapper;
    private final WebSearchToolConfig webSearchToolConfig;

    public SafetyBriefingServiceImpl(ChatClient.Builder chatClientBuilder,
                                     AiInfraMapper aiInfraMapper,
                                     WebSearchToolConfig webSearchToolConfig) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        너는 대한민국 최고의 지능형 도시 치안인프라 빅데이터 분석 전문가야.
                        제공된 가로등, 보안등, CCTV 수량 및 인근 치안시설(지구대, 파출소, 경찰서 등) 통계를 기반으로
                        안전 진단을 내려야 해. 오직 제시된 'StructuredBriefing' JSON 스키마 구조에 100% 호환되는 객체 데이터를 응답하도록 해줘.
                        답변 문자열 외부에는 마크다운 기호(```json 등)를 절대로 추가하지 마라.
                        """)
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.4)
                        .build())
                .build();
        this.aiInfraMapper = aiInfraMapper;
        this.webSearchToolConfig = webSearchToolConfig;
    }

    public StructuredBriefing generateStructuredBriefing(LocationReqDto reqDto) {
        if (reqDto.propertyId() == null) {
            throw new IllegalArgumentException("AI 안심 브리핑 분석서 생성을 하려면 매물 ID가 필수적입니다.");
        }
        String regionName = aiInfraMapper.getRegionNameByPropertyId(reqDto.propertyId());
        // 🌟 [관통의 핵심 파싱] 풀 주소에서 '동/읍/면' 뒤를 깔끔하게 잘라낸 정제 주소 추출
        String refinedKeyword = extractSearchKeyword(regionName);

        // AI에게 뉴스 검색어 결정을 맡기지 않고, 자바 코드가 먼저 네이버 뉴스를 강제로 긁어와 확보합니다.
        String newsSearchResult = webSearchToolConfig.safetyWebSearch(new WebSearchToolConfig.SearchInput(refinedKeyword));
        // 검색 결과가 정말 없을 때만 기본 방어 문구 세팅
        if (newsSearchResult == null || newsSearchResult.trim().isEmpty() || newsSearchResult.contains("결과가 확인되지 않습니다")) {
            newsSearchResult = "최근 해당 반경 웹상에서 확인되는 특이 범죄 보도는 없으며, 전반적으로 평온하고 안정적인 치안 기조를 유지하고 있는 지역입니다.";
        }

        // [1. Retrieval 단계] MySQL 공간 연산용 WKT POINT 조립 및 로컬 DB 인프라 지표 단건 추출
        String wktPoint = String.format("POINT(%f %f)", reqDto.lat(), reqDto.lon());
        // 하이브리드 RAG 데이터 조회 (가로등만 실시간 공간 계산, 나머지는 캐시 테이블 적재값 조인)
        InfraStatsDto stats = aiInfraMapper.getCombinedSafetyStats(reqDto.propertyId(), wktPoint, 1000);

        // [2. Augment 단계] 콘텍스트 구성 및 외부 정보 제약 프롬프트 주입
        String promptTemplate = """
                분석 구역 중심 좌표: 위도 {lat}, 경도 {lon}
                분석 구역 전체 주소: {regionName}
                매물 식별 ID: {propertyId}
                
                [반경 1000m 인프라 수집 데이터]
                - 방범용 CCTV: {cctv}대
                - 도로 가로등: {streetLight}개
                - 안심 보안등: {securityLight}개
                - 치안안전시설(파출소/지구대/경찰서): {policeStation}대
                
                [실시간 네이버 뉴스 수집 콘텍스트]
                {newsSearchResult}
                
                [미션 및 출력 스키마 매핑 가이드]
                위 데이터를 종합해 해당 좌표 구역의 안전 점수(0~100점) 및 등급(A~D)을 산정하고 아래 규칙에 맞게 상세 리포트 객체를 조립해줘.
                
                1. oneLineSummary: 인프라 정량 데이터와 뉴스 수집 콘텍스트를 통합하여 한 줄로 요약해. 뉴스에 특정 방범 활동(예: 안심보안관, 순찰 강화)이 있다면 그 내용을 포함해.
                2. totalReview: 뉴스 수집 콘텍스트를 주 근거로 작성해. 뉴스에 언급된 지역 치안 강화 활동(보안관 순찰, 시설 정비 등)과 인프라 데이터를 결합하여 상세히 서술해. 뉴스가 없다면 인프라 데이터만으로 서술해.
                3. positivePoints: 인프라 데이터와 뉴스 내 긍정적 치안 활동 요소를 리스트로 작성해.
                4. 인근에 관련 치안 인프라 수량이 다양하고 풍부할 경우 강점으로 부각시켜줘.
                5. 한 줄 요약이나 총평 내에서도 굳이 행정 지명을 임의로 가공해 지어내지 말고, "선택하신 좌표 구역은..." 이나 "해당 반경 구역은..." 과 같은 정형화된 공간 분석 양식을 사용해줘.
                
                [뉴스 콘텍스트 적극 반영 지침]
                - totalReview(총평) 필드를 작성할 때, 위에 제공된 '[실시간 네이버 뉴스 수집 콘텍스트]'의 내용을 반드시 핵심 근거로 삼아 문장을 구성해라.
                - 뉴스 내용에 자율방범대 순찰, 안심마을보안관 활동, 치안 조례 개정, 안심길 정비 등 지역 사회의 '치안 강화 노력 및 예방 대책' 팩트가 들어있다면, 이를 절대 생략하지 말고 총평 후반부에 구체적인 문장으로 요약하여 서술해라. (예: 최근 안심마을보안관 순찰 및 방범대 지원 확대 등 지역 내 치안 강화 조치가 적극적으로 전개되고 있어 안심할 수 있습니다.)
                - 과거의 특이 사건사고가 언급되어 있다면 이를 근거로 현재 경찰과 지자체가 방범 태세(CCTV 집중 순찰 등)를 어떻게 굳건히 다지고 있는지 뉴스 기반의 팩트 위주로 연결하여 신뢰감을 주도록 작성해라.
                - 단, 뉴스 내용에 아예 존재하지 않는 허구의 강력 범죄 소설은 단 한 구절도 지어내지 마라.
                
                최종 출력 JSON의 아래 필드에는 가이드에 적힌 인프라 수치 데이터를 왜곡 없이 숫자 그대로 매핑하여 포함시켜서 채워줘:
                - cctvCount: {cctv}
                - streetLightCount: {streetLight}
                - securityLightCount: {securityLight}
                - policeStationCount: {policeStation}
                """;

        System.out.println("RAG 기동 - 뉴스 타겟 키워드 주소: " + refinedKeyword);

        // [3. Generation 단계]
        String finalNewsSearchResult = newsSearchResult;
        return chatClient.prompt()
                .user(u -> u.text(promptTemplate)
                        .param("lat", reqDto.lat())
                        .param("lon", reqDto.lon())
                        .param("regionName", regionName)
                        .param("propertyId", String.valueOf(reqDto.propertyId()))
                        .param("newsSearchResult", finalNewsSearchResult) // 수집된 10개의 리얼 뉴스 문자열 주입
                        .param("cctv", stats.cctvCount())
                        .param("streetLight", stats.streetLightCount())
                        .param("securityLight", stats.securityLightCount())
                        .param("policeStation", stats.policeStationCount())
                )
                .call()
                .entity(StructuredBriefing.class);
    }

    /**
     * 행정구역 경계 탈출 파서 알고리즘
     * 주소를 공백으로 분해한 뒤, 도/시/구/군/동/읍/면 규격에 맞을 때만 결합하고
     * 그 외의 세부 주소 파편(로, 가, 리, 번지수)을 마주하는 즉시 안전하게 탈출합니다.
     */
    private String extractSearchKeyword(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            return "치안 구역";
        }

        String[] parts = fullAddress.split("\\s+");
        StringBuilder resultBuilder = new StringBuilder();

        for (String part : parts) {
            // 1. 장충동2가, 양평동1가 같은 비정형 동 이름 방어: 토컨 내에 '동'이 박혀있으면 '동'까지만 자르고 즉시 조기 종료
            if (part.contains("동")) {
                int dongIdx = part.indexOf("동");
                resultBuilder.append(part, 0, dongIdx + 1).append(" ");
                break;
            }

            // 2. 정형적인 행정동 최하위 마스터 레이어(동, 읍, 면)를 만나면 단어 포함 후 즉시 조기 종료
            if (part.endsWith("동") || part.endsWith("읍") || part.endsWith("면")) {
                resultBuilder.append(part).append(" ");
                break;
            }

            // 3. 상위 행정 레이어(도, 시, 구, 군)를 만나면 버퍼에 축적하고 다음 자식 레이어로 전진
            if (part.endsWith("도") || part.endsWith("시") || part.endsWith("구") || part.endsWith("군")) {
                resultBuilder.append(part).append(" ");
                continue;
            }

            // 4. 그 외에 매산로2가, 초가팔리 등 '로/가/리/지번/숫자' 레이어를 마주치면 축적된 상위 주소까지만 리턴하고 탈출
            break;
        }

        return resultBuilder.toString().trim();
    }
}