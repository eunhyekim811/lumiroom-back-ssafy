package com.ssafy.lumiroom.batch.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.ssafy.lumiroom.batch.domain.PropertyGeocodeTarget;
import com.ssafy.lumiroom.batch.dto.KakaoAddressSearchResponse;
import com.ssafy.lumiroom.batch.mapper.PropertyMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyGeocodingService {

    private static final String KAKAO_ADDRESS_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KAKAO_KEYWORD_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private final PropertyMapper propertyMapper;
    private final RestClient.Builder restClientBuilder;

    @Value("${kakao.local.rest-api-key:}")
    private String kakaoRestApiKey;

    public GeocodingResult geocodeMissingProperties(int limit, int concurrency) {
        if (!StringUtils.hasText(kakaoRestApiKey)) {
            throw new IllegalStateException("kakao.local.rest-api-key 설정이 필요합니다.");
        }

        int requestedLimit = Math.max(1, limit);
        int requestedConcurrency = Math.max(1, Math.min(concurrency, 10));
        List<PropertyGeocodeTarget> targets = propertyMapper.findPropertiesMissingCoordinates(requestedLimit);
        RestClient restClient = restClientBuilder.build();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        List<String> failureSamples = new ArrayList<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(requestedConcurrency)) {
            List<CompletableFuture<Void>> futures = targets.stream()
                    .map(target -> CompletableFuture.runAsync(
                            () -> geocodeOne(restClient, target, successCount, failCount, failureSamples),
                            executor
                    ))
                    .toList();

            futures.forEach(CompletableFuture::join);
        }

        return new GeocodingResult(targets.size(), successCount.get(), failCount.get(), failureSamples);
    }

    private void geocodeOne(
            RestClient restClient,
            PropertyGeocodeTarget target,
            AtomicInteger successCount,
            AtomicInteger failCount,
            List<String> failureSamples
    ) {
        List<String> queries = buildAddressQueries(target);
        if (queries.isEmpty()) {
            failCount.incrementAndGet();
            addFailureSample(failureSamples, target.getId() + ": 주소 후보 없음");
            return;
        }

        try {
            KakaoAddressSearchResponse.Document document = null;

            for (String query : queries) {
                KakaoAddressSearchResponse response = restClient.get()
                        .uri(KAKAO_ADDRESS_SEARCH_URL, uriBuilder -> uriBuilder
                                .queryParam("query", query)
                                .queryParam("size", 1)
                            .build())
                        .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                        .retrieve()
                        .body(KakaoAddressSearchResponse.class);

                document = firstDocument(response);
                if (document != null) {
                    break;
                }
            }

            if (document == null) {
                document = searchByKeyword(restClient, target);
            }

            if (document == null || !StringUtils.hasText(document.getX()) || !StringUtils.hasText(document.getY())) {
                failCount.incrementAndGet();
                addFailureSample(failureSamples, target.getId() + ": 검색 결과 없음, 후보=" + queries);
                return;
            }

            BigDecimal longitude = new BigDecimal(document.getX());
            BigDecimal latitude = new BigDecimal(document.getY());

            successCount.addAndGet(propertyMapper.updatePropertyCoordinates(
                    target.getId(),
                    latitude,
                    longitude
            ));
        } catch (RestClientResponseException e) {
            failCount.incrementAndGet();
            addFailureSample(failureSamples, target.getId() + ": HTTP " + e.getStatusCode() + ", " + e.getResponseBodyAsString());
        } catch (RuntimeException e) {
            failCount.incrementAndGet();
            addFailureSample(failureSamples, target.getId() + ": " + e.getMessage());
        }
    }

    private KakaoAddressSearchResponse.Document searchByKeyword(RestClient restClient, PropertyGeocodeTarget target) {
        String primaryKeyword = joinAddress(target.getRegionName(), target.getPropertyName());
        String fallbackKeyword = joinAddress(target.getSigungu(), target.getPropertyName());
        String keyword = StringUtils.hasText(primaryKeyword) ? primaryKeyword : fallbackKeyword;
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        KakaoAddressSearchResponse response = restClient.get()
                .uri(KAKAO_KEYWORD_SEARCH_URL, uriBuilder -> uriBuilder
                        .queryParam("query", keyword)
                        .queryParam("size", 1)
                        .build())
                .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                .retrieve()
                .body(KakaoAddressSearchResponse.class);

        return firstDocument(response);
    }

    private List<String> buildAddressQueries(PropertyGeocodeTarget target) {
        List<String> queries = new ArrayList<>();

        String roadAddress = joinAddress(target.getRegionName(), target.getRoadName());
        if (StringUtils.hasText(roadAddress)) {
            queries.add(roadAddress);
        }

        roadAddress = joinAddress(target.getSigungu(), target.getRoadName());
        if (StringUtils.hasText(roadAddress)) {
            queries.add(roadAddress);
        }

        String lotAddress = joinAddress(target.getRegionName(), target.getLotNumber());
        if (StringUtils.hasText(lotAddress)) {
            queries.add(lotAddress);
        }

        lotAddress = joinAddress(target.getSigungu(), target.getLotNumber());
        if (StringUtils.hasText(lotAddress)) {
            queries.add(lotAddress);
        }

        return queries.stream().distinct().toList();
    }

    private String joinAddress(String region, String detail) {
        if (!StringUtils.hasText(region) || !StringUtils.hasText(detail)) {
            return null;
        }
        return region.trim() + " " + detail.trim();
    }

    private KakaoAddressSearchResponse.Document firstDocument(KakaoAddressSearchResponse response) {
        if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
            return null;
        }
        return response.getDocuments().get(0);
    }

    private void addFailureSample(List<String> failureSamples, String message) {
        synchronized (failureSamples) {
            if (failureSamples.size() < 5) {
                failureSamples.add(message);
            }
        }
    }

    public record GeocodingResult(int targetCount, int successCount, int failCount, List<String> failureSamples) {
    }
}
