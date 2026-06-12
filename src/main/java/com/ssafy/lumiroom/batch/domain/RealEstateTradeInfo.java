package com.ssafy.lumiroom.batch.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateTradeInfo {
    private String tradeHash;
    private String sourceFile;
    private Integer noInFile;
    private String sigungu;
    private String lotNumber;
    private String mainNumber;
    private String subNumber;
    private String propertyName;
    private String propertyType;
    private String roadCondition;
    private String rentType;
    private BigDecimal exclusiveArea;
    private BigDecimal contractArea;
    private String contractYearMonth;
    private Integer contractDay;
    private String contractDate;
    private Long depositAmount;
    private Long monthlyRentAmount;
    private Long tradeAmount;
    private Integer floor;
    private Integer builtYear;
    private String roadName;
    private String canceledDate;
    private String dealType;
    private String brokerLocation;
    private String registrationDate;
    private String apartmentDongName;
    private String buyer;
    private String seller;
    private String contractPeriod;
    private String contractType;
    private String renewalRequestRight;
    private Long previousDepositAmount;
    private Long previousMonthlyRentAmount;
}
