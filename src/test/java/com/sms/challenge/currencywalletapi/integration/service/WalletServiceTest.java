package com.sms.challenge.currencywalletapi.integration.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import com.sms.challenge.currencywalletapi.persistence.repository.WalletRepository;
import com.sms.challenge.currencywalletapi.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Wallet service test.
 */
@SpringBootTest
class WalletServiceTest {

    /**
     * The Repository.
     */
    @Autowired
    WalletRepository repository;

    /**
     * The Service.
     */
    @Autowired
    WalletService service;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        CurrencyAmount currencyAmount = new CurrencyAmount("BTC", new BigDecimal(98.69));
        repository.save(new Wallet("MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet())));
    }

    /**
     * Test find.
     */
    @Test
    void testFind() {
        Long id = repository.findAll().stream().findFirst().get().getId();
        Wallet wallet = service.find(id);
        assertNotNull(wallet);
        assertEquals(wallet.getId(), id);
    }

    /**
     * Test validate find.
     */
    @Test
    void testValidateFind() {
        Exception exception1 = assertThrows(ValidationException.class, () -> service.find(null));
        assertTrue(exception1.getMessage().contains("Id is required"));

        Exception exception2 = assertThrows(NotFoundException.class, () -> service.find(2121212L));
        assertTrue(exception2.getMessage().contains("Wallet not found"));
    }

    /**
     * Test save.
     */
    @Test
    void testSave() {
        CurrencyAmount ca1 = new CurrencyAmount("BTC", new BigDecimal(98.69));
        CurrencyAmount ca2 = new CurrencyAmount("BTCA", new BigDecimal(98.79));
        Set<CurrencyAmount> currencyAmounts = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet = new Wallet("MyWallet", currencyAmounts);
        wallet = service.create(wallet);
        assertNotNull(wallet.getId());
        assertEquals(currencyAmounts.size(), wallet.getCurrencyAmounts().size());
    }

    /**
     * Test validate save.
     */
    @Test
    void testValidateSave() {
        CurrencyAmount ca1 = new CurrencyAmount("BTC", new BigDecimal(98.69));
        CurrencyAmount ca2 = new CurrencyAmount("BTC", new BigDecimal(98.79));
        Set<CurrencyAmount> currencyAmounts1 = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet = new Wallet("MyWallet", currencyAmounts1);
        Exception exception1 = assertThrows(DataIntegrityViolationException.class, () -> service.create(wallet));
        assertTrue(exception1.getCause().getCause().getMessage().contains("Unique index or primary key violation"));

        Exception exception2 = assertThrows(ValidationException.class, () -> service.create(null));
        assertTrue(exception2.getMessage().contains("Wallet is required"));

        Exception exception3 = assertThrows(ValidationException.class, () -> service.create(new Wallet(1L, "MyWallet", null)));
        assertTrue(exception3.getMessage().contains("Id is not allowed"));

        Exception exception4 = assertThrows(ValidationException.class, () -> service.create(new Wallet(null, null)));
        assertTrue(exception4.getMessage().contains("Name is required"));

        CurrencyAmount ca3 = new CurrencyAmount("BTC", null);
        Set<CurrencyAmount> currencyAmounts2 = Stream.of(ca3).collect(Collectors.toSet());

        Exception exception5 = assertThrows(ValidationException.class, () -> service.create(new Wallet("MyWallet", currencyAmounts2)));
        assertTrue(exception5.getMessage().contains("Amount is required in currency amounts"));

        CurrencyAmount ca4 = new CurrencyAmount(null, new BigDecimal(98.79));
        Set<CurrencyAmount> currencyAmounts3 = Stream.of(ca4).collect(Collectors.toSet());

        Exception exception6 = assertThrows(ValidationException.class, () -> service.create(new Wallet("MyWallet", currencyAmounts3)));
        assertTrue(exception6.getMessage().contains("Currency is required in currency amounts"));
    }

    /**
     * Test update.
     */
    @Test
    void testUpdate() {
        Wallet wallet = repository.findAll().stream().findFirst().get();
        String newName = "NewWallet";
        wallet.setName(newName);
        wallet = service.update(wallet);
        assertEquals(newName, wallet.getName());
    }

    /**
     * Test validate update.
     */
    @Test
    void testValidateUpdate() {
        Exception exception1 = assertThrows(ValidationException.class, () -> service.update(null));
        assertTrue(exception1.getMessage().contains("Wallet is required"));

        Exception exception2 = assertThrows(ValidationException.class, () -> service.update(new Wallet("MyWallet", null)));
        assertTrue(exception2.getMessage().contains("Id is required"));

        Exception exception3 = assertThrows(NotFoundException.class, () -> service.update(new Wallet(2312323L, "MyWallet", null)));
        assertTrue(exception3.getMessage().contains("Wallet not found"));

        Wallet wallet = repository.findAll().stream().findFirst().get();
        wallet.setName("");
        Exception exception4 = assertThrows(ValidationException.class, () -> service.update(wallet));
        assertTrue(exception4.getMessage().contains("Name is required"));
    }

    /**
     * Test delete.
     */
    @Test
    void testDelete() {
        Long id = repository.findAll().stream().findFirst().get().getId();
        service.delete(id);
        assertThrows(NotFoundException.class, () -> service.find(id));
    }

    /**
     * Test validate delete.
     */
    @Test
    void testValidateDelete() {
        Exception exception1 = assertThrows(ValidationException.class, () -> service.delete(null));
        assertTrue(exception1.getMessage().contains("Id is required"));

        Exception exception2 = assertThrows(NotFoundException.class, () -> service.delete(2121212L));
        assertTrue(exception2.getMessage().contains("Wallet not found"));
    }
}
