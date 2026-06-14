package com.ssafy.lumiroom.domain.model.service;

import com.ssafy.lumiroom.domain.model.dto.InfraResponse;

import java.util.List;

public interface InfraService {
    List<InfraResponse> findInfra(double lat, double lng, double radius, String types);
}
