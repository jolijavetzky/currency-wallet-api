package com.sms.challenge.currencywalletapi.bean;

import com.sms.challenge.currencywalletapi.persistence.entity.Currency;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Currency filler bean.
 */
@Component
public class CurrencyFillerBean {

    @Autowired
    private CurrencyService currencyService;

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("BTC", true));
        currencies.add(new Currency("ETH", true));
        currencies.add(new Currency("CLAM", true));
        currencies.add(new Currency("GLX", true));
        currencies.add(new Currency("USD", false));
        currencies.add(new Currency("EUR", false));
        currencies.add(new Currency("ARS", false));
        currencyService.saveAll(currencies);
    }
}
