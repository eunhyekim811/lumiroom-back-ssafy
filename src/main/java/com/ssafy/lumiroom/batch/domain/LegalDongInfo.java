package com.ssafy.lumiroom.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalDongInfo {
    private String code;
    private String name;
    private Boolean active;
    private String sidoCode;
    private String sigunguCode;
    private String dongCode;
}
