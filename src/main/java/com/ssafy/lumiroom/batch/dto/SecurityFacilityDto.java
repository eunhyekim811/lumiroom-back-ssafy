package com.ssafy.lumiroom.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityFacilityDto {

    @JsonProperty("objt_id")
    private String objtId;

    @JsonProperty("police")
    private String police;

    @JsonProperty("polcsttn")
    private String polcsttn;

    @JsonProperty("fclty_ty")
    private String fcltyTy;

    @JsonProperty("fclty_cd")
    private String fcltyCd;

    @JsonProperty("fclty_nm")
    private String fcltyNm;

    @JsonProperty("adres")
    private String adres;

    @JsonProperty("rn_adres")
    private String rnAdres;

    @JsonProperty("telno")
    private String telno;

    @JsonProperty("ctprvn_cd")
    private String ctprvnCd;

    @JsonProperty("sgg_cd")
    private String sggCd;

    @JsonProperty("x")
    private BigDecimal x;

    @JsonProperty("y")
    private BigDecimal y;

    @JsonProperty("tmp_x")
    private BigDecimal tmpX;

    @JsonProperty("tmp_y")
    private BigDecimal tmpY;
}
