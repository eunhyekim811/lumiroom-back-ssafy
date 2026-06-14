package com.ssafy.lumiroom.domain.controller;

import com.ssafy.lumiroom.domain.model.dto.InfraResponse;
import com.ssafy.lumiroom.domain.model.service.InfraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/infra")
@RequiredArgsConstructor
@Tag(name = "Infra", description = "치안 인프라 API")
public class InfraController {

    private final InfraService infraService;

    @GetMapping
    @Operation(
            summary = "치안 인프라 조회",
            description = "반경 내 CCTV, 보안등, 가로등, 경찰시설 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "치안 인프라 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InfraResponse.class)),
                            examples = @ExampleObject(
                                    name = "치안 인프라 응답 예시",
                                    value = """
                                            [
                                              {
                                                "id": 1,
                                                "type": "CCTV",
                                                "name": "광주광역시 CCTV",
                                                "lat": 35.159545,
                                                "lng": 126.852601,
                                                "address": "광주광역시 서구 치평동",
                                                "source": "공공데이터포털",
                                                "installedAt": "2024-01-15"
                                              },
                                              {
                                                "id": 2,
                                                "type": "POLICE",
                                                "name": "치평지구대",
                                                "lat": 35.152312,
                                                "lng": 126.851205,
                                                "address": "광주광역시 서구 치평동",
                                                "source": "공공데이터포털",
                                                "installedAt": null
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content)
    })
    public List<InfraResponse> getInfra(
            @Parameter(description = "위도", example = "35.159545")
            @RequestParam double lat,
            @Parameter(description = "경도", example = "126.852601")
            @RequestParam double lng,
            @Parameter(description = "조회 반경(m)", example = "500")
            @RequestParam double radius,
            @Parameter(description = "조회할 인프라 타입. 콤마로 여러 타입 지정 가능", example = "CCTV,SECURITY_LIGHT,STREET_LIGHT,POLICE")
            @RequestParam(required = false) String types) {
        return infraService.findInfra(lat, lng, radius, types);
    }
}
