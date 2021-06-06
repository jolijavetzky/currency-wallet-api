package com.sms.challenge.currencywalletapi.persistence.entity;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.persistence.*;

/**
 * The type Currency.
 */
@Data
@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_currencies")
    @SequenceGenerator(name = "sequence_currencies", sequenceName = "sequence_currencies", allocationSize = 1)
    private Long id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String symbol;

    @NonNull
    @Column(name = "crypto", nullable = false)
    private Boolean crypto;

    /**
     * Instantiates a new Currency.
     */
    public Currency() {
        super();
    }

    /**
     * Instantiates a new Currency.
     *
     * @param symbol the symbol
     * @param crypto the crypto
     */
    public Currency(String symbol, Boolean crypto) {
        this.symbol = symbol;
        this.crypto = crypto;
    }
}
