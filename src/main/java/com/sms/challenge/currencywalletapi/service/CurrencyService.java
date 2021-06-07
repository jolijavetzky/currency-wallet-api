package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.Currency;
import com.sms.challenge.currencywalletapi.persistence.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

/**
 * The type Currency service.
 */
@Service
@Transactional
public class CurrencyService {

    @Autowired
    private CurrencyRepository repository;

    /**
     * Find all list.
     *
     * @return the list
     */
    public List<Currency> findAll() {
        return repository.findAll();
    }

    /**
     * Find by symbol currency.
     *
     * @param symbol the symbol
     * @return the currency
     */
    public Currency findBySymbol(String symbol) {
        if (StringUtils.isEmpty(symbol)) {
            throw new ValidationException("Symbol is required");
        }
        Currency currency = repository.findBySymbol(symbol);
        if (currency == null) {
            throw new NotFoundException("Currency not found");
        }
        return currency;
    }

    /**
     * Save all.
     *
     * @param currencies the currencies
     */
    public void saveAll(List<Currency> currencies) {
        repository.saveAll(currencies);
    }

    /**
     * Find all by crypto list.
     *
     * @return the list
     */
    public List<Currency> findAllByCrypto() {
        return repository.findByCryptoTrue();
    }

    /**
     * Find all by not crypto list.
     *
     * @return the list
     */
    public List<Currency> findAllByNotCrypto() {
        return repository.findByCryptoFalse();
    }
}
