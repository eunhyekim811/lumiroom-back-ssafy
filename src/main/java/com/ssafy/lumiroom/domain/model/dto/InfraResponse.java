package com.ssafy.lumiroom.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfraResponse {
    private Long id;
    private String type;
    private String name;
    private Double lat;
    private Double lng;
    private String address;
    private String source;
    private LocalDate installedAt;
}
