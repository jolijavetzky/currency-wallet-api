package com.sms.challenge.currencywalletapi.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * The type Crypto currency price.
 */
@Data
public class CryptoCurrencyPrice implements Serializable {

    private String currency;
    private Double price;

    /**
     * Instantiates a new Crypto currency price.
     *
     * @param currency the currency
     * @param price    the price
     */
    public CryptoCurrencyPrice(String currency, Double price) {
        this.currency = currency;
        this.price = price;
    }
}
