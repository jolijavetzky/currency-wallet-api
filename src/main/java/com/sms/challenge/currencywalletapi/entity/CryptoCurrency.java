package com.sms.challenge.currencywalletapi.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Crypto currency.
 */
@Data
public class CryptoCurrency implements Serializable {

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
