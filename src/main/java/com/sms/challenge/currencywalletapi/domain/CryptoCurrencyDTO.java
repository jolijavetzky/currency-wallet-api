package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;

import java.util.List;

/**
 * The type Crypto currency dto.
 */
@Data
public class CryptoCurrencyDTO {

    private String currency;
    private List<CryptoCurrencyPriceDTO> prices;
}
