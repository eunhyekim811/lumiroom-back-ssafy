package com.ssafy.lumiroom.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyListItemResponse {

    private Long id;
    private String propertyName;
    private Integer builtYear;
    private BigDecimal safetyScore;
    private String safetyGrade;
    private int cctvCount;
    private int securityLightCount;
    private int securityFacilityCount;
    private String rentType;
    private String transactionType;
    private Long minDepositAmount;
    private Long maxDepositAmount;
    private Long minMonthlyRentAmount;
    private Long maxMonthlyRentAmount;
    private Long minTradeAmount;
    private Long maxTradeAmount;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
