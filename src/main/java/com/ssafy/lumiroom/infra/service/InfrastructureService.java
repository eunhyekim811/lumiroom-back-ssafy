package com.ssafy.lumiroom.infra.service;

import com.ssafy.lumiroom.infra.dto.InfraReqDto;
import com.ssafy.lumiroom.infra.dto.InfraResDto;

import java.util.List;

public interface InfrastructureService {

    public List<InfraResDto> getMarkers(InfraReqDto dto);
}
