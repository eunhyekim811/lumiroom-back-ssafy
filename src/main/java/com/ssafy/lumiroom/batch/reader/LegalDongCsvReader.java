package com.ssafy.lumiroom.batch.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.ssafy.lumiroom.batch.dto.LegalDongDto;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

public class LegalDongCsvReader implements ItemStreamReader<LegalDongDto> {

    private final String csvPath;
    private final Charset charset;
    private final DelimitedLineTokenizer tokenizer;

    private BufferedReader reader;
    private Map<String, Integer> headerIndex;

    public LegalDongCsvReader(String csvPath, Charset charset) {
        this.csvPath = csvPath;
        this.charset = charset;
        this.tokenizer = new DelimitedLineTokenizer();
        this.tokenizer.setDelimiter(",");
        this.tokenizer.setQuoteCharacter('"');
        this.tokenizer.setStrict(false);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            Resource resource = new PathMatchingResourcePatternResolver().getResource(csvPath);
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), charset));
            headerIndex = null;
        } catch (IOException e) {
            throw new ItemStreamException("Failed to open legal dong CSV resource: " + csvPath, e);
        }
    }

    @Override
    public LegalDongDto read() throws Exception {
        while (reader != null) {
            String line = reader.readLine();
            if (line == null) {
                return null;
            }

            if (!StringUtils.hasText(line)) {
                continue;
            }

            FieldSet fieldSet = tokenizer.tokenize(line);
            if (headerIndex == null) {
                headerIndex = createHeaderIndex(fieldSet);
                continue;
            }

            LegalDongDto dto = mapRow(fieldSet);
            if (StringUtils.hasText(dto.getCode()) && StringUtils.hasText(dto.getName())) {
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

    private Map<String, Integer> createHeaderIndex(FieldSet fieldSet) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < fieldSet.getFieldCount(); i++) {
            index.put(normalizeHeader(fieldSet.readString(i)), i);
        }
        return index;
    }

    private LegalDongDto mapRow(FieldSet fieldSet) {
        LegalDongDto dto = new LegalDongDto();
        dto.setCode(read(fieldSet, "법정동코드"));
        dto.setName(read(fieldSet, "법정동명"));
        dto.setStatus(read(fieldSet, "폐지여부"));
        return dto;
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
