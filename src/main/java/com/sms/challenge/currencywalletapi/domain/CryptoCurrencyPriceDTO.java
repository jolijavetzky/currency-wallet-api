package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;

/**
 * The type Crypto currency price dto.
 */
@Data
public class CryptoCurrencyPriceDTO {

    private String currency;
    private Double price;

    /**
     * Instantiates a new Crypto currency price dto.
     *
     * @param currency the currency
     * @param price    the price
     */
    public CryptoCurrencyPriceDTO(String currency, Double price) {
        this.currency = currency;
        this.price = price;
    }

    /**
     * Builder crypto currency price dto . builder.
     *
     * @return the crypto currency price dto . builder
     */
    public static CryptoCurrencyPriceDTO.Builder builder() {
        return new CryptoCurrencyPriceDTO.Builder();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private String currency;
        private Double price;

        /**
         * Currency crypto currency price dto . builder.
         *
         * @param currency the currency
         * @return the crypto currency price dto . builder
         */
        public CryptoCurrencyPriceDTO.Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Price crypto currency price dto . builder.
         *
         * @param price the price
         * @return the crypto currency price dto . builder
         */
        public CryptoCurrencyPriceDTO.Builder price(Double price) {
            this.price = price;
            return this;
        }

        /**
         * Build crypto currency price dto.
         *
         * @return the crypto currency price dto
         */
        public CryptoCurrencyPriceDTO build() {
            return new CryptoCurrencyPriceDTO(this.currency, this.price);
        }
    }
}
