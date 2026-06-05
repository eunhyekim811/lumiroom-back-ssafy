package com.ssafy.lumiroom.batch.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityLightInfo {
    private String sn;
    private String fcltType;
    private String fcltCd;
    private String fcltGvmnfcNm;
    private String addr;
    private String roadNmAddr;
    private String stdgCtpvCd;
    private String stdgSggCd;
    private String stdgEmdCd;
    private String locationWkt; // POINT(X Y) 포맷 저장용
}