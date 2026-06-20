package com.ssafy.lumiroom.batch.processor;

import com.ssafy.lumiroom.batch.domain.LegalDongInfo;
import com.ssafy.lumiroom.batch.dto.LegalDongDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LegalDongItemProcessor implements ItemProcessor<LegalDongDto, LegalDongInfo> {

    @Override
    public LegalDongInfo process(LegalDongDto dto) {
        String code = digitsOnly(dto.getCode());
        String name = clean(dto.getName());

        if (code.length() != 10 || name == null) {
            return null;
        }

        return LegalDongInfo.builder()
                .code(code)
                .name(name)
                .active("존재".equals(clean(dto.getStatus())))
                .sidoCode(code.substring(0, 2))
                .sigunguCode(code.substring(0, 5))
                .dongCode(code)
                .build();
    }

    private String clean(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.replace("\uFEFF", "").trim();
    }

    private String digitsOnly(String value) {
        String cleaned = clean(value);
        if (cleaned == null) {
            return "";
        }
        return cleaned.replaceAll("[^0-9]", "");
    }
}
