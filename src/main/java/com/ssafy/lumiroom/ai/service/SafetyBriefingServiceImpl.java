package com.ssafy.lumiroom.ai.service;

import com.ssafy.lumiroom.ai.dao.AiInfraMapper;
import com.ssafy.lumiroom.ai.dto.InfraStatsDto;
import com.ssafy.lumiroom.ai.dto.LocationReqDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SafetyBriefingServiceImpl implements SafetyBriefingService{

    private final ChatClient chatClient;
    private final AiInfraMapper aiInfraMapper;

    // ChatClient.Builder를 주입받아 빌드합니다.
    public SafetyBriefingServiceImpl(ChatClient.Builder chatClientBuilder, AiInfraMapper aiInfraMapper) {
        this.chatClient = chatClientBuilder
                .defaultSystem("너는 'LumiRoom' 서비스의 최고 부동산 안전 분석가야. 제공된 데이터를 기반으로 객관적이고 따뜻한 어조로 안전 브리핑을 작성해줘.")
                .build();
        this.aiInfraMapper = aiInfraMapper;
    }

    public String generateBriefing(LocationReqDto reqDto) {
        // [1. Retrieval (검색)] : DB에서 내 주변 500m 인프라 조회
        String wktPoint = String.format("POINT(%f %f)", reqDto.lat(), reqDto.lon());
        InfraStatsDto stats = aiInfraMapper.getSafetyInfraStats(wktPoint, 500);

        // [2. Augment (증강)] : 프롬프트 템플릿에 데이터 삽입
        String promptTemplate = """
                사용자가 조회한 지역({region})의 반경 500m 이내 안전 인프라 현황입니다.
                - 가로등: {streetLight}개
                - 보안등: {securityLight}개
                - CCTV: {cctv}개
                
                위 데이터를 바탕으로 이곳을 방문하거나 거주하려는 사람을 위해 'LumiRoom 안전 브리핑' 보고서를 작성해줘.
                인프라가 풍부하다면 안심할 수 있도록 설명하고, 부족하다면 밤길 주의 등 현실적인 조언을 포함해줘.
                결과는 마크다운 형식을 사용해 가독성 좋게 출력해줘.
                """;

        // [3. Generation (생성)] : GMS 프록시 서버를 통한 LLM 호출
        return chatClient.prompt()
                .user(u -> u.text(promptTemplate)
                        .param("region", reqDto.regionName())
                        .param("streetLight", stats.streetLightCount())
                        .param("securityLight", stats.securityLightCount())
                        .param("cctv", stats.cctvCount())
                )
                .call()
                .content();
    }
}