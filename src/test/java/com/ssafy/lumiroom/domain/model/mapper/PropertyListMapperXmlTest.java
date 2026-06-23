package com.ssafy.lumiroom.domain.model.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyListMapperXmlTest {

    @Test
    void joinsPropertySafetySummaryByPropertyId() throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/mapper/PropertyListMapper.xml")) {
            assertThat(input).isNotNull();

            String mapperXml = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(mapperXml)
                    .contains("p.built_year")
                    .contains("pss.safety_score")
                    .contains("pss.safety_grade")
                    .contains("LEFT JOIN property_safety_summary pss")
                    .contains("ON pss.property_id = p.id")
                    .doesNotContain("region_safety_masters");
        }
    }
}
