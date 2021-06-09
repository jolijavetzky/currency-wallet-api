package com.sms.challenge.currencywalletapi.integration.repository;

import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
import com.sms.challenge.currencywalletapi.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Wallet repository test.
 */
@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository repository;

    /**
     * Test find by id.
     */
    @Test
    public void testFindById() {
        Wallet wallet = new Wallet("MyWallet", new HashSet<>());
        repository.save(wallet);
        assertNotNull(wallet.getId());
        assertTrue(repository.findById(wallet.getId()).isPresent());
    }

    /**
     * Test save.
     */
    @Test
    public void testSave() {
        Set<CurrencyAmount> currencyAmounts = new HashSet<>();
        currencyAmounts.add(new CurrencyAmount("BTC1", 98.69));
        currencyAmounts.add(new CurrencyAmount("BTC2", 98.79));
        currencyAmounts.add(new CurrencyAmount("BTC3", 98.69));
        Wallet wallet = new Wallet("MyWallet", currencyAmounts);
        repository.save(wallet);
        assertNotNull(wallet.getId());
        assertEquals(currencyAmounts.size(), wallet.getCurrencyAmounts().size());
    }

    /**
     * Test validate save.
     */
    @Test
    public void testValidateSave() {
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> repository.save(new Wallet()));
        assertTrue(exception.getMessage().contains("not-null property references a null or transient value"));

        Set<CurrencyAmount> currencyAmounts1 = new HashSet<>();
        currencyAmounts1.add(new CurrencyAmount(null, 98.69));
        Wallet wallet1 = new Wallet("MyWallet", currencyAmounts1);
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(wallet1));

        Set<CurrencyAmount> currencyAmounts2 = new HashSet<>();
        currencyAmounts2.add(new CurrencyAmount("BTC", null));
        Wallet wallet2 = new Wallet("MyWallet", currencyAmounts2);
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(wallet2));

        Set<CurrencyAmount> currencyAmounts3 = new HashSet<>();
        currencyAmounts3.add(new CurrencyAmount(null, null));
        Wallet wallet3 = new Wallet("MyWallet", currencyAmounts3);
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(wallet3));
    }

    /**
     * Test update.
     */
    @Test
    public void testUpdate() {
        Wallet wallet = new Wallet("MyWallet", new HashSet<>());
        repository.save(wallet);
        assertNotNull(wallet.getId());
        wallet = repository.findById(wallet.getId()).get();
        String newName = "Pedro";
        wallet.setName(newName);
        wallet.getCurrencyAmounts().add(new CurrencyAmount("BTC", 98.69));
        repository.save(wallet);
        assertEquals(newName, wallet.getName());
        assertEquals(1, wallet.getCurrencyAmounts().size());
    }

    /**
     * Test delete.
     */
    @Test
    public void testDelete() {
        Wallet wallet = new Wallet("MyWallet", new HashSet<>());
        repository.save(wallet);
        assertNotNull(wallet.getId());
        repository.delete(wallet);
        assertFalse(repository.findById(wallet.getId()).isPresent());
    }
}
