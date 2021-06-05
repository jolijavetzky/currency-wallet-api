package com.sms.challenge.currencywalletapi.integration.service;

import com.sms.challenge.currencywalletapi.domain.CurrencyAmountDTO;
import com.sms.challenge.currencywalletapi.domain.WalletDTO;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Wallet service test.
 */
@SpringBootTest
class WalletServiceTest {

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
        Set<CurrencyAmount> currencyAmounts = new HashSet<>();
        currencyAmounts.add(new CurrencyAmount("BTC", new BigDecimal(98.69)));
        repository.save(new Wallet("MyWallet", currencyAmounts));
    }

    /**
     * Test find.
     */
    @Test
    void testFind() {
        Long id = repository.findAll().stream().findFirst().get().getId();
        WalletDTO wallet = service.find(id);
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
        Set<CurrencyAmountDTO> currencyAmounts = new HashSet<>();
        currencyAmounts.add(new CurrencyAmountDTO("BTC", new BigDecimal(98.69)));
        currencyAmounts.add(new CurrencyAmountDTO("BTCA", new BigDecimal(98.79)));
        WalletDTO wallet = new WalletDTO("MyWallet", currencyAmounts);
        wallet = service.create(wallet);
        assertNotNull(wallet.getId());
        assertEquals(currencyAmounts.size(), wallet.getCurrencyAmounts().size());
    }

    /**
     * Test validate save.
     */
    @Test
    void testValidateSave() {
        Set<CurrencyAmountDTO> currencyAmounts1 = new HashSet<>();
        currencyAmounts1.add(new CurrencyAmountDTO("BTC", new BigDecimal(98.69)));
        currencyAmounts1.add(new CurrencyAmountDTO("BTC", new BigDecimal(98.79)));
        WalletDTO wallet = new WalletDTO("MyWallet", currencyAmounts1);
        Exception exception1 = assertThrows(DataIntegrityViolationException.class, () -> service.create(wallet));
        assertTrue(exception1.getCause().getCause().getMessage().contains("Unique index or primary key violation"));

        Exception exception2 = assertThrows(ValidationException.class, () -> service.create(null));
        assertTrue(exception2.getMessage().contains("Wallet is required"));

        Exception exception3 = assertThrows(ValidationException.class, () -> service.create(new WalletDTO(1L, "MyWallet", null)));
        assertTrue(exception3.getMessage().contains("Id is not allowed"));

        Exception exception4 = assertThrows(ValidationException.class, () -> service.create(new WalletDTO(null, null)));
        assertTrue(exception4.getMessage().contains("Name is required"));

        Set<CurrencyAmountDTO> currencyAmounts2 = new HashSet<>();
        currencyAmounts2.add(new CurrencyAmountDTO("BTC", null));
        Exception exception5 = assertThrows(ValidationException.class, () -> service.create(new WalletDTO("MyWallet", currencyAmounts2)));
        assertTrue(exception5.getMessage().contains("Amount is required in currency amounts"));

        Set<CurrencyAmountDTO> currencyAmounts3 = new HashSet<>();
        currencyAmounts3.add(new CurrencyAmountDTO(null, new BigDecimal(98.79)));
        Exception exception6 = assertThrows(ValidationException.class, () -> service.create(new WalletDTO("MyWallet", currencyAmounts3)));
        assertTrue(exception6.getMessage().contains("Currency is required in currency amounts"));
    }

    /**
     * Test update.
     */
    @Test
    void testUpdate() {
        Long id = repository.findAll().stream().findFirst().get().getId();
        String newName = "NewWallet";
        WalletDTO wallet = new WalletDTO(id, newName, null);
        wallet = service.update(wallet);
        assertEquals(newName, wallet.getName());
    }

    /**
     * Test validate update.
     */
    @Test
    void testValidateUpdate() {
        Long id = repository.findAll().stream().findFirst().get().getId();

        Exception exception1 = assertThrows(ValidationException.class, () -> service.update(null));
        assertTrue(exception1.getMessage().contains("Wallet is required"));

        Exception exception2 = assertThrows(ValidationException.class, () -> service.update(new WalletDTO("MyWallet", null)));
        assertTrue(exception2.getMessage().contains("Id is required"));

        Exception exception3 = assertThrows(NotFoundException.class, () -> service.update(new WalletDTO(2312323L, "MyWallet", null)));
        assertTrue(exception3.getMessage().contains("Wallet not found"));

        Exception exception4 = assertThrows(ValidationException.class, () -> service.update(new WalletDTO(id, null, null)));
        assertTrue(exception4.getMessage().contains("Name is required"));

        Set<CurrencyAmountDTO> currencyAmounts1 = new HashSet<>();
        currencyAmounts1.add(new CurrencyAmountDTO("BTC1", new BigDecimal(98.69)));
        currencyAmounts1.add(new CurrencyAmountDTO("BTC2", new BigDecimal(98.79)));
        Exception exception5 = assertThrows(ValidationException.class, () -> service.update(new WalletDTO(id, "MyWallet", currencyAmounts1)));
        assertTrue(exception5.getMessage().contains("Currency amounts are not allowed"));
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
