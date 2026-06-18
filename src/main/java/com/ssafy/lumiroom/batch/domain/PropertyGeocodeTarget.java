package com.ssafy.lumiroom.batch.domain;

import lombok.Data;

@Data
public class PropertyGeocodeTarget {
    private Long id;
    private String regionName;
    private String sigungu;
    private String propertyName;
    private String roadName;
    private String lotNumber;
}
