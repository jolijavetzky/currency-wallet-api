package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.entity.CryptoCurrency;
import com.sms.challenge.currencywalletapi.entity.CryptoCurrencyPrice;
import com.sms.challenge.currencywalletapi.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<String> currenciesFrom = this.currencyService.findAllByCrypto().stream().map(Currency::getSymbol).collect(
                Collectors.toList());
        List<String> currenciesTo = this.currencyService.findAllByNotCrypto().stream().map(Currency::getSymbol).collect(
                Collectors.toList());
        List<CryptoCurrency> result = new ArrayList<>();
        Map<String, Map<String, Double>> data = this.fetcherService.fetch(currenciesFrom, currenciesTo);
        currenciesFrom.stream().forEach(itemFrom -> {
            if (data.get(itemFrom) != null) {
                CryptoCurrency cryptoCurrency = new CryptoCurrency(itemFrom);
                cryptoCurrency.setPrices(new ArrayList<>());
                currenciesTo.stream().forEach(itemTo -> cryptoCurrency.getPrices().add(new CryptoCurrencyPrice(
                        itemTo,
                        data.get(itemFrom).get(itemTo)
                )));
                result.add(cryptoCurrency);
            }
        });
        return result;
    }

    /**
     * Find crypto currency.
     *
     * @param currency the currency
     * @return the crypto currency
     */
    @Cacheable("currency")
    public CryptoCurrency find(String currency) {
        List<String> currenciesTo = this.currencyService.findAllByNotCrypto().stream().map(Currency::getSymbol).collect(
                Collectors.toList());
        Map<String, Map<String, Double>> data = this.fetcherService.fetch(
                Stream.of(currency.toUpperCase()).collect(Collectors.toList()),
                currenciesTo
        );
        CryptoCurrency cryptoCurrency = new CryptoCurrency(currency.toUpperCase());
        cryptoCurrency.setPrices(new ArrayList<>());
        Map<String, Double> currencyData = data.get(currency.toUpperCase());
        if (currencyData != null) {
            currenciesTo.forEach(itemTo ->
                    cryptoCurrency.getPrices().add(new CryptoCurrencyPrice(
                            itemTo,
                            currencyData.get(itemTo)
                    ))
            );
        }
        return cryptoCurrency;
    }

    /**
     * Convert double.
     *
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @return the double
     */
    @Cacheable("conversion")
    public Double convert(String currencyFrom, String currencyTo) {
        Map<String, Double> data = this.fetcherService.fetch(currencyFrom, currencyTo);
        return data.get(currencyTo.toUpperCase());
    }
}
