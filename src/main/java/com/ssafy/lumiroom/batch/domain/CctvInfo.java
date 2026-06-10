package com.ssafy.lumiroom.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CctvInfo {   // DB에 넘겨줄 순수 자바 객체
    private String managementNo;
    private String openGovCode;
    private String agencyName;
    private String roadAddress;
    private String lotAddress;
    private String purpose;
    private Integer cameraCount;
    private Integer pixels;
    private String direction;
    private Integer storageDays;
    private String installYm;
    private String phone;
    private String baseDate;

    // MySQL의 ST_GeomFromText에 전달할 문자열. 예: "POINT(126.9736 37.57865)"
    private String locationWkt;
}