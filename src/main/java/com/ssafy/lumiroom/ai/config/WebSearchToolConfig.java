package com.ssafy.lumiroom.ai.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class WebSearchToolConfig {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public record SearchInput(String query) {}

    @Tool(description = "특정 지역의 최신 치안, 범죄, 안전 관련 뉴스와 정보를 웹에서 검색합니다. 파라미터 query에는 제공받은 추천 뉴스 검색어 주소를 그대로 입력하세요.")
    public String safetyWebSearch(SearchInput input) {
        String rawQuery = input.query();
        System.out.println("[LLM이 도구로 전달한 주소 검색어]: " + rawQuery);

        try {
            // LLM이 간혹 수식어를 중복해서 붙여올 때를 대비한 최소한의 클리닝
            String cleanQuery = rawQuery.replaceAll("(치안|범죄|안전|뉴스|사건|사고|종합|정보|트렌드)", "").trim();

            if (cleanQuery.isEmpty()) {
                cleanQuery = rawQuery;
            }

            String finalSearchWord = cleanQuery + " 치안";
            System.out.println("[네이버 뉴스 최종 검색어]: " + finalSearchWord);

            // 기존의 URLEncoder.encode()와 String apiURL 문장 조립법을 완전히 폐기하고,
            // 스프링이 제공하는 UriComponentsBuilder를 사용해 'URI 객체'를 직접 생성합니다.
            // 이렇게 하면 RestTemplate이 주소창의 % 기호를 %25로 더블 인코딩하는 버그를 완벽하게 방어합니다.
            URI uri = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/news.json")
                    .queryParam("query", finalSearchWord)
                    .queryParam("display", 10) // 3년치 데이터 수집을 위한 확장
                    .queryParam("sort", "sim")  // 과거 기사 탐색을 위한 유사도순 정렬
                    .build()
                    .encode(StandardCharsets.UTF_8) // 안전한 UTF-8 인코딩 처리
                    .toUri(); // 최종 URI 객체 반환

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            // 🌟 수술 부위: 첫 번째 인자로 문자열(apiURL)이 아닌, 위에서 빌드한 'uri 객체'를 전달합니다.
            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
            System.out.println("[더블 인코딩 방어 후 네이버 API 응답 데이터]: " + response.getBody());

            JsonNode items = response.getBody().get("items");
            StringBuilder resultBuilder = new StringBuilder();

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String title = item.get("title").asText().replaceAll("<[^>]*>", "");
                    String description = item.get("description").asText().replaceAll("<[^>]*>", "");
                    resultBuilder.append("- ").append(title).append(" : ").append(description).append("\n");
                }
            }

            String finalResult = resultBuilder.toString().trim();

            if (finalResult.isEmpty()) {
                return "해당 지역 구역의 최근 특이한 강력 범죄나 치안 관련 뉴스 검색 결과가 확인되지 않습니다.";
            }

            return finalResult;

        } catch (Exception e) {
            System.err.println("네이버 뉴스 검색 중 예외 발생: " + e.getMessage());
            return "실시간 검색 API 연동 지연으로 외부 최신 뉴스를 참조하지 못했습니다.";
        }
    }
}