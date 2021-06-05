package com.sms.challenge.currencywalletapi.domain;

import com.sms.challenge.currencywalletapi.persistence.entity.CurrencyAmount;
import lombok.Data;

import java.math.BigDecimal;

/**
 * The type Currency amount dto.
 */
@Data
public class CurrencyAmountDTO {

    private String currency;
    private BigDecimal amount;

    /**
     * Instantiates a new Currency amount dto.
     *
     * @param currency the currency
     * @param amount   the amount
     */
    public CurrencyAmountDTO(String currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    /**
     * Builder currency amount dto . builder.
     *
     * @return the currency amount dto . builder
     */
    public static CurrencyAmountDTO.Builder builder() {
        return new CurrencyAmountDTO.Builder();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private String currency;
        private BigDecimal amount;

        /**
         * Currency currency amount . builder.
         *
         * @param currency the currency
         * @return the currency amount . builder
         */
        public CurrencyAmountDTO.Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Amount currency amount . builder.
         *
         * @param amount the amount
         * @return the currency amount . builder
         */
        public CurrencyAmountDTO.Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Build currency amount dto.
         *
         * @return the currency amount dto
         */
        public CurrencyAmountDTO build() {
            return new CurrencyAmountDTO(this.currency, this.amount);
        }
    }
}
