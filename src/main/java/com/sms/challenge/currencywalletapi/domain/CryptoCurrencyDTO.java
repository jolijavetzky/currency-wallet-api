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

    /**
     * Instantiates a new Crypto currency dto.
     *
     * @param currency the currency
     * @param prices   the prices
     */
    public CryptoCurrencyDTO(String currency, List<CryptoCurrencyPriceDTO> prices) {
        this.currency = currency;
        this.prices = prices;
    }

    /**
     * Builder crypto currency dto . builder.
     *
     * @return the crypto currency dto . builder
     */
    public static CryptoCurrencyDTO.Builder builder() {
        return new CryptoCurrencyDTO.Builder();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private String currency;
        private List<CryptoCurrencyPriceDTO> prices;

        /**
         * Name crypto currency dto . builder.
         *
         * @param currency the currency
         * @return the crypto currency dto . builder
         */
        public CryptoCurrencyDTO.Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Prices crypto currency dto . builder.
         *
         * @param prices the prices
         * @return the crypto currency dto . builder
         */
        public CryptoCurrencyDTO.Builder prices(List<CryptoCurrencyPriceDTO> prices) {
            this.prices = prices;
            return this;
        }

        /**
         * Build crypto currency dto.
         *
         * @return the crypto currency dto
         */
        public CryptoCurrencyDTO build() {
            return new CryptoCurrencyDTO(this.currency, this.prices);
        }
    }
}
