package com.sms.challenge.currencywalletapi.integration.service;

import com.sms.challenge.currencywalletapi.service.CryptoCurrencyService;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The type Crypto currency service test.
 */
@SpringBootTest
public class CryptoCurrencyServiceTest {

    /**
     * The Service.
     */
    @Autowired
    CryptoCurrencyService service;

    /**
     * Test find all.
     */
    @Test
    void testFindAll() {
        this.service.findAll();
    }
}
