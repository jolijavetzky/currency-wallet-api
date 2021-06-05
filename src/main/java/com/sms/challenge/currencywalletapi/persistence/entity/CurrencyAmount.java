package com.sms.challenge.currencywalletapi.persistence.entity;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The type Currency amount.
 */
@Data
@Entity
@Table(name = "currency_amounts", uniqueConstraints = @UniqueConstraint(columnNames = {"currency", "wallet_id"}))
public class CurrencyAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_currency_amounts")
    @SequenceGenerator(name = "sequence_currency_amounts", sequenceName = "sequence_currency_amounts", allocationSize = 1)
    private Long id;

    @NonNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @NonNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * Instantiates a new Currency amount.
     */
    public CurrencyAmount() {
        super();
    }

    /**
     * Instantiates a new Currency amount.
     *
     * @param currency the currency
     * @param amount   the amount
     */
    public CurrencyAmount(String currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    /**
     * Builder currency amount . builder.
     *
     * @return the currency amount . builder
     */
    public static CurrencyAmount.Builder builder() {
        return new CurrencyAmount.Builder();
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
        public CurrencyAmount.Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Amount currency amount . builder.
         *
         * @param amount the amount
         * @return the currency amount . builder
         */
        public CurrencyAmount.Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Build currency amount.
         *
         * @return the currency amount
         */
        public CurrencyAmount build() {
            return new CurrencyAmount(this.currency, this.amount);
        }
    }
}
