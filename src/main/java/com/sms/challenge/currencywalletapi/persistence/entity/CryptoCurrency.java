package com.sms.challenge.currencywalletapi.persistence.entity;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * The type Crypto currency.
 */
@Data
public class CryptoCurrency {

    private String currency;
    private List<CryptoCurrencyPrice> prices;

    /**
     * Instantiates a new Crypto currency.
     *
     * @param currency the currency
     */
    public CryptoCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Instantiates a new Crypto currency.
     *
     * @param currency the currency
     * @param prices   the prices
     */
    public CryptoCurrency(String currency, List<CryptoCurrencyPrice> prices) {
        this.currency = currency;
        this.prices = prices;
    }
}
