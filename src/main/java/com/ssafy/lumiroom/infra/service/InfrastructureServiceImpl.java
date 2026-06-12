package com.ssafy.lumiroom.infra.service;

import com.ssafy.lumiroom.infra.dao.InfrastructureMapper;
import com.ssafy.lumiroom.infra.dto.InfraReqDto;
import com.ssafy.lumiroom.infra.dto.InfraResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfrastructureServiceImpl implements InfrastructureService{

    private final InfrastructureMapper infrastructureMapper;

    public List<InfraResDto> getMarkers(InfraReqDto dto) {
        return infrastructureMapper.getInfraMarkersByBounds(dto);
    }
}