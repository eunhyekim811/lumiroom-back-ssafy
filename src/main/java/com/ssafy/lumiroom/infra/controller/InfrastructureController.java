package com.ssafy.lumiroom.infra.controller;

import com.ssafy.lumiroom.infra.dto.InfraReqDto;
import com.ssafy.lumiroom.infra.dto.InfraResDto;
import com.ssafy.lumiroom.infra.service.InfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/infra")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class InfrastructureController {

    private final InfrastructureService infrastructureService;

    @GetMapping("/markers")
    public ResponseEntity<List<InfraResDto>> getMarkers(@ModelAttribute InfraReqDto dto) {
        List<InfraResDto> markers = infrastructureService.getMarkers(dto);
        return ResponseEntity.ok(markers);
    }
}