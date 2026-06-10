package com.ssafy.lumiroom.batch.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class StreetLightInfo {
    private String sn;
    private String fcltType;
    private String fcltCd;
    private String fcltGvmnfcNm;
    private String addr;
    private String roadNmAddr;
    private String stdgCtpvCd;
    private String stdgSggCd;
    private String stdgEmdCd;
    private String locationWkt; // POINT(경도 위도) 형식 문자열
}