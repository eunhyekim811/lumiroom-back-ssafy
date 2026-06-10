package com.ssafy.lumiroom.batch.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.lumiroom.batch.dto.StreetLightDto;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class StreetLightApiReader implements ItemReader<StreetLightDto> {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String baseUrl;
    private final String serviceKey;
    private final int chunkSize;

    private int currentPage;
    private final Queue<StreetLightDto> currentChunkData = new LinkedList<>();

    public StreetLightApiReader(String baseUrl, String serviceKey, int startPage, int chunkSize) {
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
        this.currentPage = startPage;
        this.chunkSize = chunkSize;
        this.objectMapper = new ObjectMapper();

        this.restTemplate = new RestTemplate();
        // WAF 방화벽 우회를 위한 진짜 브라우저 위장 헤더 인터셉터 장착
        this.restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            return execution.execute(request, body);
        }));
    }

    @Override
    public StreetLightDto read() throws Exception {
        if (currentChunkData.isEmpty()) {
            fetchNextPage();
        }
        return currentChunkData.poll(); // 큐에서 하나씩 빼서 Processor로 전달
    }

    private void fetchNextPage() {
        try {
            // URL 파라미터 인코딩 오염 방지를 위해 URI 객체 사용
            String urlStr = baseUrl + "?serviceKey=" + serviceKey +
                    "&pageNo=" + currentPage +
                    "&numOfRows=" + chunkSize +
                    "&returnType=json";

            URI uri = new URI(urlStr);
            System.out.println(">>> [Reader] API 호출 URI: " + uri);

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode bodyNode = rootNode.path("body"); // 가로등 응답의 실제 데이터 배열 노드

            if (bodyNode.isArray() && bodyNode.size() > 0) {
                System.out.println(">>> [Reader] " + currentPage + "페이지 수집 성공, 가져온 데이터 수: " + bodyNode.size());
                for (JsonNode node : bodyNode) {
                    StreetLightDto dto = objectMapper.treeToValue(node, StreetLightDto.class);
                    currentChunkData.add(dto);
                }
                currentPage++;

                // IP 차단을 막기 위해 다음 페이지 호출 전 휴식
                Thread.sleep(2000);
            } else {
                System.out.println(">>> [Reader] 데이터 수집 종료 또는 빈 응답 수신 (Page: " + currentPage + ")");
            }

        } catch (Exception e) {
            System.err.println(">>> [Reader Error] API 호출 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("API 데이터 호출 중 예외 발생", e);
        }
    }
}