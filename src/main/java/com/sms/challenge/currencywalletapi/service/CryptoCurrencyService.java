package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.entity.CryptoCurrency;
import com.sms.challenge.currencywalletapi.entity.CryptoCurrencyPrice;
import com.sms.challenge.currencywalletapi.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Crypto currency service.
 */
@Service
public class CryptoCurrencyService {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CryptoCurrencyFetcherService fetcherService;

    /**
     * Find all list.
     *
     * @return the list
     */
    @Cacheable("currencies")
    public List<CryptoCurrency> findAll() {
        List<String> currenciesFrom = this.currencyService.findAllByCrypto().stream().map(Currency::getSymbol).collect(Collectors.toList());
        List<String> currenciesTo = this.currencyService.findAllByNotCrypto().stream().map(Currency::getSymbol).collect(Collectors.toList());
        List<CryptoCurrency> result = new ArrayList<>();
        Map<String, Map<String, Double>> data = this.fetcherService.fetch(currenciesFrom, currenciesTo);
        currenciesFrom.stream().forEach(itemFrom -> {
            if (data.get(itemFrom) != null) {
                CryptoCurrency cryptoCurrency = new CryptoCurrency(itemFrom);
                cryptoCurrency.setPrices(new ArrayList<>());
                currenciesTo.stream().forEach(itemTo -> cryptoCurrency.getPrices().add(new CryptoCurrencyPrice(itemTo, data.get(itemFrom).get(itemTo))));
                result.add(cryptoCurrency);
            }
        });
        return result;
    }

    /**
     * Convert double.
     *
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @return the double
     */
    @Cacheable("currency")
    public Double convert(String currencyFrom, String currencyTo) {
        Map<String, Double> data = this.fetcherService.fetch(currencyFrom, currencyTo);
        if (data.isEmpty()) {
            throw new NotFoundException("Currency symbol not found");
        }
        return data.get(currencyTo);
    }
}
