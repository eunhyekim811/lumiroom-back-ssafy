package com.ssafy.lumiroom.batch.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Builder
@ToString
public class SecurityFacilityInfo {
    private String objtId;
    private String police;
    private String polcsttn;
    private String fcltyTy;
    private String fcltyCd;
    private String fcltyNm;
    private String adres;
    private String rnAdres;
    private String telno;
    private String ctprvnCd;
    private String sggCd;
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal tmpX;
    private BigDecimal tmpY;
}
