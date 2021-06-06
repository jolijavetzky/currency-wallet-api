package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * The type Crypto currency price dto.
 */
@Data
public class CryptoCurrencyPriceDTO {

    private String currency;
    private Double price;
}
