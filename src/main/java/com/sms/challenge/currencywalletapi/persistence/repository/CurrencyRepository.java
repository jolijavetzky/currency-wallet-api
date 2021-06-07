package com.sms.challenge.currencywalletapi.persistence.repository;

import com.sms.challenge.currencywalletapi.persistence.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * The interface Currency repository.
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    /**
     * Find by symbol currency.
     *
     * @param symbol the symbol
     * @return the currency
     */
    Currency findBySymbol(String symbol);

    /**
     * Find by crypto true list.
     *
     * @return the list
     */
    List<Currency> findByCryptoTrue();

    /**
     * Find by crypto false list.
     *
     * @return the list
     */
    List<Currency> findByCryptoFalse();
}
