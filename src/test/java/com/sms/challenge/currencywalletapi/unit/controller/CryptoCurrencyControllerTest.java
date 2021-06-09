package com.sms.challenge.currencywalletapi.unit.controller;

import com.sms.challenge.currencywalletapi.controller.CryptoCurrencyController;
import com.sms.challenge.currencywalletapi.entity.CryptoCurrency;
import com.sms.challenge.currencywalletapi.entity.CryptoCurrencyPrice;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyFetcherService;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyService;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Crypto currency controller test.
 */
@WebMvcTest(CryptoCurrencyController.class)
public class CryptoCurrencyControllerTest {

    private static final String CRYPTO_CURRENCY_SYMBOL = "BTC";
    private static final String NOT_CRYPTO_CURRENCY_SYMBOL_1 = "USD";
    private static final String NOT_CRYPTO_CURRENCY_SYMBOL_2 = "EUR";
    private static final Double NOT_CRYPTO_CURRENCY_PRICE_1 = 65.32;
    private static final Double NOT_CRYPTO_CURRENCY_PRICE_2 = 48.25;

    /**
     * The Service.
     */
    @MockBean
    CryptoCurrencyService service;

    @MockBean
    CryptoCurrencyFetcherService fetcherService;

    @MockBean
    CurrencyService currencyService;

    /**
     * The Mock mvc.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * Test find all.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFindAll() throws Exception {
        List<CryptoCurrencyPrice> prices = Stream.of(
                new CryptoCurrencyPrice(NOT_CRYPTO_CURRENCY_SYMBOL_1, NOT_CRYPTO_CURRENCY_PRICE_1),
                new CryptoCurrencyPrice(NOT_CRYPTO_CURRENCY_SYMBOL_2, NOT_CRYPTO_CURRENCY_PRICE_2)
        ).collect(Collectors.toList());
        CryptoCurrency cryptoCurrency = new CryptoCurrency(CRYPTO_CURRENCY_SYMBOL, prices);
        List<CryptoCurrency> cryptoCurrencies = Stream.of(cryptoCurrency).collect(Collectors.toList());
        when(this.service.findAll()).thenReturn(cryptoCurrencies);

        mockMvc.perform(get("/crypto-currencies")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$[*].currency").isNotEmpty())
                .andExpect(jsonPath("$[*].prices").isNotEmpty());

    }
}
