package com.sms.challenge.currencywalletapi.integration.repository;

import com.sms.challenge.currencywalletapi.persistence.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Currency repository test.
 */
@DataJpaTest
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository repository;

    /**
     * Test find by crypto true.
     */
    @Test
    public void testFindByCryptoTrue() {
        long size = repository.findAll().stream().filter(item -> item.getCrypto()).count();
        assertEquals(size, repository.findByCryptoTrue().size());

    }

    /**
     * Test find by crypto false.
     */
    @Test
    public void testFindByCryptoFalse() {
        long size = repository.findAll().stream().filter(item -> !item.getCrypto()).count();
        assertEquals(size, repository.findByCryptoFalse().size());
    }
}
