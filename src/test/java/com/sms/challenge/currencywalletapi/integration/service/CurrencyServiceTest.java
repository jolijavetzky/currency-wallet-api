package com.sms.challenge.currencywalletapi.integration.service;

import com.sms.challenge.currencywalletapi.persistence.entity.Currency;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Currency service test.
 */
@SpringBootTest
public class CurrencyServiceTest {

    /**
     * The Service.
     */
    @Autowired
    CurrencyService service;

    /**
     * Test save all.
     */
    @Test
    void testSaveAll() {
        int previousSize = service.findAll().size();
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("ALIEN", true));
        currencies.add(new Currency("ARS", false));
        service.saveAll(currencies);
        assertEquals(previousSize + currencies.size(), service.findAll().size());
    }

    /**
     * Test find all by crypto.
     */
    @Test
    void testFindAllByCrypto() {
        long size = service.findAll().stream().filter(item -> item.getCrypto()).count();
        assertEquals(size, service.findAllByCrypto().size());
    }

    /**
     * Test find all by not crypto.
     */
    @Test
    void testFindAllByNotCrypto() {
        long size = service.findAll().stream().filter(item -> !item.getCrypto()).count();
        assertEquals(size, service.findAllByNotCrypto().size());
    }

}
