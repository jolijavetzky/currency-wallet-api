package com.sms.challenge.currencywalletapi.persistence.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * The type Wallet.
 */
@Data
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_wallets")
    @SequenceGenerator(name = "sequence_wallets", sequenceName = "sequence_wallets", allocationSize = 1)
    private Long id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private Set<CurrencyAmount> currencyAmounts;

    /**
     * The Created at.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    /**
     * The Updated at.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    /**
     * Instantiates a new Wallet.
     */
    public Wallet() {
        super();
    }

    /**
     * Instantiates a new Wallet.
     *
     * @param name the name
     */
    public Wallet(String name) {
        this.name = name;
    }

    /**
     * Instantiates a new Wallet.
     *
     * @param name            the name
     * @param currencyAmounts the currency amounts
     */
    public Wallet(String name, Set<CurrencyAmount> currencyAmounts) {
        this.name = name;
        this.currencyAmounts = currencyAmounts;
    }

    /**
     * Instantiates a new Wallet.
     *
     * @param id              the id
     * @param name            the name
     * @param currencyAmounts the currency amounts
     */
    public Wallet(Long id, String name, Set<CurrencyAmount> currencyAmounts) {
        this.id = id;
        this.name = name;
        this.currencyAmounts = currencyAmounts;
    }

    /**
     * Builder wallet . builder.
     *
     * @return the wallet . builder
     */
    public static Wallet.Builder builder() {
        return new Wallet.Builder();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private String name;
        private Set<CurrencyAmount> currencyAmounts;

        /**
         * Name builder.
         *
         * @param name the name
         * @return the builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Currency amounts builder.
         *
         * @param currencyAmounts the currency amounts
         * @return the builder
         */
        public Builder currencyAmounts(Set<CurrencyAmount> currencyAmounts) {
            this.currencyAmounts = currencyAmounts;
            return this;
        }

        /**
         * Build wallet.
         *
         * @return the wallet
         */
        public Wallet build() {
            return new Wallet(this.name, this.currencyAmounts);
        }
    }
}
