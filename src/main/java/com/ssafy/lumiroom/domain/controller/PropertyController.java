package com.ssafy.lumiroom.domain.controller;

import com.ssafy.lumiroom.domain.model.dto.PropertyListResponse;
import com.ssafy.lumiroom.domain.model.service.PropertyListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Property", description = "실거래 매물 API")
public class PropertyController {

    private final PropertyListService propertyListService;

    @GetMapping
    @Operation(
            summary = "주변 실거래 매물 목록 조회",
            description = "지도 중심 좌표와 반경을 기준으로 주변 매물을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매물 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public PropertyListResponse getProperties(
            @Parameter(description = "지도 중심 위도", example = "35.159545")
            @RequestParam double lat,
            @Parameter(description = "지도 중심 경도", example = "126.852601")
            @RequestParam double lng,
            @Parameter(description = "조회 반경(m), 최대 50000", example = "1000")
            @RequestParam(defaultValue = "1000") double radius,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기, 최대 500", example = "100")
            @RequestParam(defaultValue = "100") int size) {
        return propertyListService.findProperties(lat, lng, radius, page, size);
    }
}
