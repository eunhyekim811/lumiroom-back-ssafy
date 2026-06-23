package com.ssafy.lumiroom.domain.model.service;

import com.ssafy.lumiroom.domain.model.dto.PropertyListItemResponse;
import com.ssafy.lumiroom.domain.model.dto.PropertyListResponse;

public interface PropertyListService {

    PropertyListResponse findProperties(double lat, double lng, double radius, int page, int size);
    
    PropertyListItemResponse getPropertyDetail(Long id);
}
