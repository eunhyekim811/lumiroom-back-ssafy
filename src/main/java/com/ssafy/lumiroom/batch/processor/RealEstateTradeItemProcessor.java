package com.ssafy.lumiroom.batch.processor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

import com.ssafy.lumiroom.batch.domain.RealEstateTradeInfo;
import com.ssafy.lumiroom.batch.dto.RealEstateTradeDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RealEstateTradeItemProcessor implements ItemProcessor<RealEstateTradeDto, RealEstateTradeInfo> {

    @Override
    public RealEstateTradeInfo process(RealEstateTradeDto dto) {
        String contractDate = parseContractDate(dto.getContractYearMonth(), dto.getContractDay());
        if (!StringUtils.hasText(dto.getSigungu()) || contractDate == null) {
            return null;
        }

        String propertyType = clean(dto.getHouseType());
        if (propertyType == null) {
            propertyType = inferPropertyType(dto.getSourceFile());
        }

        return RealEstateTradeInfo.builder()
                .tradeHash(createTradeHash(dto))
                .sourceFile(clean(dto.getSourceFile()))
                .noInFile(parseInteger(dto.getNo()))
                .sigungu(clean(dto.getSigungu()))
                .lotNumber(clean(dto.getLotNumber()))
                .mainNumber(clean(dto.getMainNumber()))
                .subNumber(clean(dto.getSubNumber()))
                .propertyName(clean(dto.getBuildingName()))
                .propertyType(propertyType)
                .roadCondition(clean(dto.getRoadCondition()))
                .rentType(clean(dto.getRentType()))
                .exclusiveArea(parseBigDecimal(dto.getExclusiveArea()))
                .contractArea(parseBigDecimal(dto.getContractArea()))
                .contractYearMonth(clean(dto.getContractYearMonth()))
                .contractDay(parseInteger(dto.getContractDay()))
                .contractDate(contractDate)
                .depositAmount(parseLong(dto.getDepositAmount()))
                .monthlyRentAmount(parseLong(dto.getMonthlyRentAmount()))
                .tradeAmount(parseLong(dto.getTradeAmount()))
                .floor(parseInteger(dto.getFloor()))
                .builtYear(parseInteger(dto.getBuiltYear()))
                .roadName(clean(dto.getRoadName()))
                .canceledDate(parseOptionalDate(dto.getCanceledDate()))
                .dealType(clean(dto.getDealType()))
                .brokerLocation(clean(dto.getBrokerLocation()))
                .registrationDate(parseOptionalDate(dto.getRegistrationDate()))
                .apartmentDongName(clean(dto.getApartmentDongName()))
                .buyer(clean(dto.getBuyer()))
                .seller(clean(dto.getSeller()))
                .contractPeriod(clean(dto.getContractPeriod()))
                .contractType(clean(dto.getContractType()))
                .renewalRequestRight(clean(dto.getRenewalRequestRight()))
                .previousDepositAmount(parseLong(dto.getPreviousDepositAmount()))
                .previousMonthlyRentAmount(parseLong(dto.getPreviousMonthlyRentAmount()))
                .build();
    }

    private String parseContractDate(String yearMonth, String day) {
        String normalizedYearMonth = digitsOnly(yearMonth);
        Integer normalizedDay = parseInteger(day);
        if (normalizedYearMonth.length() != 6 || normalizedDay == null || normalizedDay < 1 || normalizedDay > 31) {
            return null;
        }

        return normalizedYearMonth.substring(0, 4)
                + "-"
                + normalizedYearMonth.substring(4, 6)
                + "-"
                + String.format("%02d", normalizedDay);
    }

    private String parseOptionalDate(String value) {
        String digits = digitsOnly(value);
        if (digits.length() != 8) {
            return null;
        }
        return digits.substring(0, 4) + "-" + digits.substring(4, 6) + "-" + digits.substring(6, 8);
    }

    private Integer parseInteger(String value) {
        String cleaned = cleanNumber(value);
        if (cleaned == null) {
            return null;
        }
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        String cleaned = cleanNumber(value);
        if (cleaned == null) {
            return null;
        }
        try {
            return Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        String cleaned = cleanNumber(value);
        if (cleaned == null) {
            return null;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String cleanNumber(String value) {
        String cleaned = clean(value);
        if (cleaned == null) {
            return null;
        }
        return cleaned.replace(",", "");
    }

    private String clean(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String cleaned = value.trim();
        if ("-".equals(cleaned)) {
            return null;
        }
        return cleaned;
    }

    private String digitsOnly(String value) {
        String cleaned = clean(value);
        if (cleaned == null) {
            return "";
        }
        return cleaned.replaceAll("[^0-9]", "");
    }

    private String inferPropertyType(String sourceFile) {
        if (sourceFile == null) {
            return null;
        }
        if (sourceFile.contains("officetel")) {
            return "오피스텔";
        }
        if (sourceFile.contains("single_multi_house")) {
            return "단독다가구";
        }
        if (sourceFile.contains("multi_family")) {
            return "연립다세대";
        }
        return null;
    }

    private String createTradeHash(RealEstateTradeDto dto) {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(nullToEmpty(dto.getSourceFile()));
        joiner.add(nullToEmpty(dto.getSigungu()));
        joiner.add(nullToEmpty(dto.getLotNumber()));
        joiner.add(nullToEmpty(dto.getBuildingName()));
        joiner.add(nullToEmpty(dto.getRentType()));
        joiner.add(nullToEmpty(dto.getExclusiveArea()));
        joiner.add(nullToEmpty(dto.getContractArea()));
        joiner.add(nullToEmpty(dto.getContractYearMonth()));
        joiner.add(nullToEmpty(dto.getContractDay()));
        joiner.add(nullToEmpty(dto.getDepositAmount()));
        joiner.add(nullToEmpty(dto.getMonthlyRentAmount()));
        joiner.add(nullToEmpty(dto.getTradeAmount()));
        joiner.add(nullToEmpty(dto.getFloor()));
        joiner.add(nullToEmpty(dto.getRoadName()));
        return sha256(joiner.toString());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
