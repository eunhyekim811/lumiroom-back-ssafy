package com.ssafy.lumiroom.batch.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.ssafy.lumiroom.batch.dto.RealEstateTradeDto;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

public class RealEstateTradeCsvReader implements ItemStreamReader<RealEstateTradeDto> {

    private final String csvPattern;
    private final Charset charset;
    private final DelimitedLineTokenizer tokenizer;

    private Resource[] resources = new Resource[0];
    private int resourceIndex;
    private BufferedReader reader;
    private Map<String, Integer> headerIndex;
    private String currentFileName;

    public RealEstateTradeCsvReader(String csvPattern, Charset charset) {
        this.csvPattern = csvPattern;
        this.charset = charset;
        this.tokenizer = new DelimitedLineTokenizer();
        this.tokenizer.setDelimiter(",");
        this.tokenizer.setQuoteCharacter('"');
        this.tokenizer.setStrict(false);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            resources = new PathMatchingResourcePatternResolver().getResources(csvPattern);
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareTo)));
            resourceIndex = 0;
            openNextResource();
        } catch (IOException e) {
            throw new ItemStreamException("Failed to open real estate trade CSV resources: " + csvPattern, e);
        }
    }

    @Override
    public RealEstateTradeDto read() throws Exception {
        while (reader != null) {
            String line = reader.readLine();
            if (line == null) {
                openNextResource();
                continue;
            }

            if (!StringUtils.hasText(line)) {
                continue;
            }

            FieldSet fieldSet = tokenizer.tokenize(line);
            if (headerIndex == null) {
                if (isHeader(fieldSet)) {
                    headerIndex = createHeaderIndex(fieldSet);
                }
                continue;
            }

            RealEstateTradeDto dto = mapRow(fieldSet);
            if (StringUtils.hasText(dto.getSigungu())) {
                return dto;
            }
        }

        return null;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
        closeCurrentReader();
    }

    private void openNextResource() throws IOException {
        closeCurrentReader();
        headerIndex = null;

        if (resourceIndex >= resources.length) {
            reader = null;
            currentFileName = null;
            return;
        }

        Resource resource = resources[resourceIndex++];
        currentFileName = resource.getFilename();
        reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), charset));
    }

    private void closeCurrentReader() {
        if (reader == null) {
            return;
        }

        try {
            reader.close();
        } catch (IOException ignored) {
        } finally {
            reader = null;
        }
    }

    private boolean isHeader(FieldSet fieldSet) {
        for (int i = 0; i < fieldSet.getFieldCount(); i++) {
            if ("NO".equals(normalizeHeader(fieldSet.readString(i)))) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Integer> createHeaderIndex(FieldSet fieldSet) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < fieldSet.getFieldCount(); i++) {
            index.put(normalizeHeader(fieldSet.readString(i)), i);
        }
        return index;
    }

    private RealEstateTradeDto mapRow(FieldSet fieldSet) {
        RealEstateTradeDto dto = new RealEstateTradeDto();
        dto.setSourceFile(currentFileName);
        dto.setNo(read(fieldSet, "NO"));
        dto.setSigungu(read(fieldSet, "시군구"));
        dto.setLotNumber(read(fieldSet, "번지"));
        dto.setMainNumber(read(fieldSet, "본번"));
        dto.setSubNumber(read(fieldSet, "부번"));
        dto.setBuildingName(readFirst(fieldSet, "단지명", "건물명"));
        dto.setRoadCondition(read(fieldSet, "도로조건"));
        dto.setRentType(read(fieldSet, "전월세구분"));
        dto.setExclusiveArea(read(fieldSet, "전용면적(㎡)"));
        dto.setContractArea(read(fieldSet, "계약면적(㎡)"));
        dto.setContractYearMonth(read(fieldSet, "계약년월"));
        dto.setContractDay(read(fieldSet, "계약일"));
        dto.setDepositAmount(read(fieldSet, "보증금(만원)"));
        dto.setMonthlyRentAmount(read(fieldSet, "월세금(만원)"));
        dto.setTradeAmount(read(fieldSet, "거래금액(만원)"));
        dto.setFloor(read(fieldSet, "층"));
        dto.setBuiltYear(read(fieldSet, "건축년도"));
        dto.setRoadName(read(fieldSet, "도로명"));
        dto.setCanceledDate(read(fieldSet, "해제사유발생일"));
        dto.setDealType(read(fieldSet, "거래유형"));
        dto.setBrokerLocation(read(fieldSet, "중개사소재지"));
        dto.setRegistrationDate(read(fieldSet, "등기일자"));
        dto.setApartmentDongName(read(fieldSet, "아파트동명"));
        dto.setBuyer(read(fieldSet, "매수자"));
        dto.setSeller(read(fieldSet, "매도자"));
        dto.setContractPeriod(read(fieldSet, "계약기간"));
        dto.setContractType(read(fieldSet, "계약구분"));
        dto.setRenewalRequestRight(read(fieldSet, "갱신요구권 사용"));
        dto.setPreviousDepositAmount(read(fieldSet, "종전계약 보증금(만원)"));
        dto.setPreviousMonthlyRentAmount(read(fieldSet, "종전계약 월세(만원)"));
        dto.setHouseType(read(fieldSet, "주택유형"));
        return dto;
    }

    private String readFirst(FieldSet fieldSet, String... names) {
        for (String name : names) {
            String value = read(fieldSet, name);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String read(FieldSet fieldSet, String name) {
        Integer index = headerIndex.get(normalizeHeader(name));
        if (index == null || index >= fieldSet.getFieldCount()) {
            return null;
        }
        return fieldSet.readString(index).trim();
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\uFEFF", "").trim();
    }
}
