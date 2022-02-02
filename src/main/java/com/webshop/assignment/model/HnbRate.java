package com.webshop.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HnbRate {

    @JsonProperty("Broj tečajnice")
    private String numberOfExchangeRate;

    @JsonProperty("Datum primjene")
    private String applicationDate;

    @JsonProperty("Država")
    private String country;

    @JsonProperty("Šifra valute")
    private String currencyCode;

    @JsonProperty("Valuta")
    private String currency;

    @JsonProperty("Jedinica")
    private String unit;

    @JsonProperty("Kupovni za devize")
    private String exchangeRatePurchasing;

    @JsonProperty("Srednji za devize")
    private String exchangeRateMiddle;

    @JsonProperty("Prodajni za devize")
    private String exchangeRateSelling;
}
