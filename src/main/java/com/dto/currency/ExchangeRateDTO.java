package com.dto.currency;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateDTO {

    @JsonAlias("eur")
    private BigDecimal euro;

    @JsonAlias("gbp")
    private BigDecimal britishPound;

    @JsonAlias("chf")
    private BigDecimal swissFranc;

    @JsonAlias("jpy")
    private BigDecimal japaneseYen;

    @JsonAlias("try")
    private BigDecimal turkishLira;
}
