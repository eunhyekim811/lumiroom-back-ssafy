package com.ssafy.lumiroom.batch.dto;

import lombok.Data;

@Data
public class CctvDto {
    private String openGovCode;
    private String managementNo;
    private String agencyName;
    private String roadAddress;
    private String lotAddress;
    private String purpose;
    private String cameraCount;
    private String pixels;
    private String direction;
    private String storageDays;
    private String installYm;
    private String phone;
    private String lat;  // 위도
    private String lon;  // 경도
    private String baseDate;
    private String updateType;
    private String updateTime;
    private String lastModifiedTime;
}