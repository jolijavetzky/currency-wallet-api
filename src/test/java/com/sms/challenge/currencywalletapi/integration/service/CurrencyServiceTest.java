package com.sms.challenge.currencywalletapi.integration.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.entity.Currency;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Currency service test.
 */
@SpringBootTest
public class CurrencyServiceTest {

    private static final String CURRENCY_SYMBOL_NON_EXISTENT = "non-existent";

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
        Currency c1 = new Currency("ALIEN", true);
        Currency c2 = new Currency("BRL", false);
        List<Currency> currencies = Stream.of(c1, c2).collect(Collectors.toList());
        service.saveAll(currencies);
        assertEquals(previousSize + currencies.size(), service.findAll().size());
    }

    /**
     * Test find by symbol.
     */
    @Test
    void testFindBySymbol() {
        String symbol = service.findAll().stream().findAny().get().getSymbol();
        Currency currency = service.findBySymbol(symbol);
        assertNotNull(currency);
        assertEquals(symbol, currency.getSymbol());
    }

    /**
     * Test validate find by symbol.
     */
    @Test
    void testValidateFindBySymbol() {
        Exception exception1 = assertThrows(ValidationException.class, () -> service.findBySymbol(null));
        assertTrue(exception1.getMessage().contains("Symbol is required"));

        Exception exception2 = assertThrows(NotFoundException.class, () -> service.findBySymbol(CURRENCY_SYMBOL_NON_EXISTENT));
        assertTrue(exception2.getMessage().contains("Currency not found"));
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
