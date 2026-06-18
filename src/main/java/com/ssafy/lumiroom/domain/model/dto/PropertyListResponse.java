package com.ssafy.lumiroom.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PropertyListResponse {

    private List<PropertyListItemResponse> properties;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
