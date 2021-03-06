package com.sms.challenge.currencywalletapi.unit.service;

import com.sms.challenge.currencywalletapi.entity.CryptoCurrency;
import com.sms.challenge.currencywalletapi.entity.Currency;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyFetcherService;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyService;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * The type Crypto currency service test.
 */
@SpringBootTest
class CryptoCurrencyServiceTest {

    private static final String CRYPTO_CURRENCY_SYMBOL = "BTC";
    private static final String NOT_CRYPTO_CURRENCY_SYMBOL_1 = "USD";
    private static final String NOT_CRYPTO_CURRENCY_SYMBOL_2 = "EUR";
    private static final Double NOT_CRYPTO_CURRENCY_PRICE_1 = 65.32;
    private static final Double NOT_CRYPTO_CURRENCY_PRICE_2 = 48.25;

    /**
     * The Fetcher service.
     */
    @Mock
    CryptoCurrencyFetcherService fetcherService;

    /**
     * The Currency service.
     */
    @Mock
    CurrencyService currencyService;

    /**
     * The Service.
     */
    @InjectMocks
    CryptoCurrencyService service;

    /**
     * Sets mock output.
     */
    @BeforeEach
    void setMockOutput() {
        Currency c1 = new Currency(CRYPTO_CURRENCY_SYMBOL, true);
        Currency c2 = new Currency(NOT_CRYPTO_CURRENCY_SYMBOL_1, false);
        Currency c3 = new Currency(NOT_CRYPTO_CURRENCY_SYMBOL_2, false);
        when(this.currencyService.findAllByCrypto()).thenReturn(Stream.of(c1).collect(Collectors.toList()));
        when(this.currencyService.findAllByNotCrypto()).thenReturn(Stream.of(c2, c3).collect(Collectors.toList()));
        Map<String, Map<String, Number>> currencies = new HashMap<>();
        Map<String, Number> prices = new HashMap<>();
        prices.put(NOT_CRYPTO_CURRENCY_SYMBOL_1, NOT_CRYPTO_CURRENCY_PRICE_1);
        prices.put(NOT_CRYPTO_CURRENCY_SYMBOL_2, NOT_CRYPTO_CURRENCY_PRICE_2);
        currencies.put(CRYPTO_CURRENCY_SYMBOL, prices);
        when(this.fetcherService.fetch(Mockito.anyList(), Mockito.anyList())).thenReturn(currencies);
        when(this.fetcherService.fetch(Mockito.anyString(), Mockito.anyString())).thenReturn(prices);
    }

    /**
     * Test find all.
     */
    @Test
    void testFindAll() {
        List<CryptoCurrency> cryptoCurrencies = this.service.findAll();
        assertNotNull(cryptoCurrencies);
        assertFalse(cryptoCurrencies.isEmpty());
    }

    /**
     * Test find.
     */
    @Test
    void testFind() {
        CryptoCurrency cryptoCurrency = this.service.find(CRYPTO_CURRENCY_SYMBOL);
        assertNotNull(cryptoCurrency);
    }

    /**
     * Test validate find.
     */
    @Test
    void testValidateFind() {
        when(this.fetcherService.fetch(Mockito.anyList(), Mockito.anyList())).thenThrow(new NotFoundException(
                "Currency symbol not found"));
        Exception exception = assertThrows(NotFoundException.class, () -> this.service.find(CRYPTO_CURRENCY_SYMBOL));
        assertTrue(exception.getMessage().contains("Currency symbol not found"));
    }

    /**
     * Test convert.
     */
    @Test
    void testConvert() {
        Number value = this.service.convert(CRYPTO_CURRENCY_SYMBOL, NOT_CRYPTO_CURRENCY_SYMBOL_1);
        assertNotNull(value);
        assertEquals(NOT_CRYPTO_CURRENCY_PRICE_1, value);
    }

    /**
     * Test validate convert.
     */
    @Test
    void testValidateConvert() {
        when(this.fetcherService.fetch(Mockito.anyString(), Mockito.anyString())).thenThrow(new NotFoundException(
                "Currency symbol not found"));
        Exception exception = assertThrows(NotFoundException.class, () -> this.service.convert("BTC", "USD"));
        assertTrue(exception.getMessage().contains("Currency symbol not found"));
    }
}
