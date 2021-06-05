package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;

import java.util.Set;

/**
 * The type Wallet dto.
 */
@Data
public class WalletDTO {

    private Long id;
    private String name;
    private Set<CurrencyAmountDTO> currencyAmounts;

    /**
     * Instantiates a new Wallet dto.
     *
     * @param name            the name
     * @param currencyAmounts the currency amounts
     */
    public WalletDTO(String name, Set<CurrencyAmountDTO> currencyAmounts) {
        this.name = name;
        this.currencyAmounts = currencyAmounts;
    }

    /**
     * Instantiates a new Wallet dto.
     *
     * @param id              the id
     * @param name            the name
     * @param currencyAmounts the currency amounts
     */
    public WalletDTO(Long id, String name, Set<CurrencyAmountDTO> currencyAmounts) {
        this.id = id;
        this.name = name;
        this.currencyAmounts = currencyAmounts;
    }

    /**
     * Builder wallet dto . builder.
     *
     * @return the wallet dto . builder
     */
    public static WalletDTO.Builder builder() {
        return new WalletDTO.Builder();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private Long id;
        private String name;
        private Set<CurrencyAmountDTO> currencyAmounts;

        /**
         * Id wallet dto . builder.
         *
         * @param id the id
         * @return the wallet dto . builder
         */
        public WalletDTO.Builder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Name builder.
         *
         * @param name the name
         * @return the builder
         */
        public WalletDTO.Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Currency amounts builder.
         *
         * @param currencyAmounts the currency amounts
         * @return the builder
         */
        public WalletDTO.Builder currencyAmounts(Set<CurrencyAmountDTO> currencyAmounts) {
            this.currencyAmounts = currencyAmounts;
            return this;
        }

        /**
         * Build wallet.
         *
         * @return the wallet
         */
        public WalletDTO build() {
            return new WalletDTO(this.id, this.name, this.currencyAmounts);
        }
    }
}
