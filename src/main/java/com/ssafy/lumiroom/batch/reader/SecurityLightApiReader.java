package com.ssafy.lumiroom.batch.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.lumiroom.batch.dto.SecurityLightDto;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class SecurityLightApiReader implements ItemReader<SecurityLightDto> {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;
    private final String serviceKey;

    private int pageNo;
    private final int numOfRows = 1000;
    private boolean isFinished = false;
    private Queue<SecurityLightDto> currentChunkData = new LinkedList<>();

    public SecurityLightApiReader(String baseUrl, String serviceKey, int startPage){
        this.baseUrl=baseUrl;
        this.serviceKey=serviceKey;
        this.pageNo=startPage;

        // 방화벽 우회
        this.restTemplate = new RestTemplate();

        // 브라우저로 위장하는 '헤더 인터셉터' 장착!
        this.restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.add(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
            headers.add(HttpHeaders.CONNECTION, "keep-alive");
            return execution.execute(request, body);
        }));
    }

    @Override
    public SecurityLightDto read() throws Exception {
        if (isFinished) {
            return null; // 배치가 끝났음을 알림
        }

        if (currentChunkData.isEmpty()) {
            fetchNextPage();
        }

        if (currentChunkData.isEmpty()) {
            isFinished = true;
            return null;
        }

        return currentChunkData.poll(); // 큐에서 하나씩 꺼내서 Processor로 전달
    }

    private void fetchNextPage() {
        try {
            // API 호출 URL 조립
            String url = String.format("%s?serviceKey=%s&pageNo=%d&numOfRows=%d&returnType=json",
                    baseUrl, serviceKey, pageNo, numOfRows);
            java.net.URI uri = new java.net.URI(url);

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

//            System.out.println("====== [API RAW RESPONSE] ======");
//            System.out.println(response.getBody());
//            System.out.println("=================================");

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode itemsNode = rootNode.path("body");

            if (itemsNode.isArray() && itemsNode.size() > 0) {
                System.out.println(">>> [Reader] " + pageNo + "페이지 수집 성공, 가져온 데이터 수: " + itemsNode.size());
                for (JsonNode node : itemsNode) {
                    SecurityLightDto dto = objectMapper.treeToValue(node, SecurityLightDto.class);
                    currentChunkData.offer(dto);
                }
                pageNo++; // 다음 페이지를 위해 증가
            } else {
                isFinished = true; // 더 이상 가져올 데이터가 없음
            }

            // API Rate Limit 방지를 위한 딜레이
            Thread.sleep(2000);

        } catch (Exception e) {
            throw new RuntimeException("API 데이터 호출 중 에러 발생", e);
        }
    }
}