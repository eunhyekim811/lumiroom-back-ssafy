package com.ssafy.lumiroom.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityLightDto {
    @JsonProperty("SN")
    private String sn;

    @JsonProperty("FCLT_TYPE")
    private String fcltType;

    @JsonProperty("FCLT_CD")
    private String fcltCd;

    @JsonProperty("FCLT_GVMNFC_NM")
    private String fcltGvmnfcNm;

    @JsonProperty("ADDR")
    private String addr;

    @JsonProperty("ROAD_NM_ADDR")
    private String roadNmAddr;

    @JsonProperty("STDG_CTPV_CD")
    private String stdgCtpvCd;

    @JsonProperty("STDG_SGG_CD")
    private String stdgSggCd;

    @JsonProperty("STDG_EMD_CD")
    private String stdgEmdCd;

    @JsonProperty("XMAP_CRTS")
    private Double xmapCrts;

    @JsonProperty("YMAP_CRTS")
    private Double ymapCrts;
}