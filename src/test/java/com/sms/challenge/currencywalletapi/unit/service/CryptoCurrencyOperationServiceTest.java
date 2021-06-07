package com.sms.challenge.currencywalletapi.unit.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.Currency;
import com.sms.challenge.currencywalletapi.persistence.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyOperationService;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyService;
import com.sms.challenge.currencywalletapi.service.CurrencyService;
import com.sms.challenge.currencywalletapi.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * The type Crypto currency operation service test.
 */
@SpringBootTest
public class CryptoCurrencyOperationServiceTest {

    private static final String CURRENCY_SYMBOL_FROM = "BTC";
    private static final String CURRENCY_SYMBOL_TO = "USD";
    private static final Double CURRENCY_INITIAL_AMOUNT = 2.5;
    private static final Double CURRENCY_AMOUNT_TO_BUY = 1.2;
    private static final Double CURRENCY_PRICE = 36000.0;
    private static final String CURRENCY_SYMBOL_NON_EXISTENT = "non-existent";

    /**
     * The Currency service.
     */
    @Mock
    CurrencyService currencyService;

    /**
     * The Crypto currency service.
     */
    @Mock
    CryptoCurrencyService cryptoCurrencyService;

    /**
     * The Wallet service.
     */
    @Mock
    WalletService walletService;

    /**
     * The Service.
     */
    @InjectMocks
    CryptoCurrencyOperationService service;

    /**
     * Sets mock output.
     */
    @BeforeEach
    void setMockOutput() {
        when(this.walletService.update(Mockito.any())).thenReturn(new Wallet());
        when(this.currencyService.findBySymbol(Mockito.anyString())).thenReturn(new Currency());
        when(this.currencyService.findBySymbol(CURRENCY_SYMBOL_NON_EXISTENT)).thenReturn(null);
        when(this.cryptoCurrencyService.convert(Mockito.anyString(), Mockito.anyString())).thenReturn(CURRENCY_PRICE);
    }

    /**
     * Test buy currency to exists.
     */
    @Test
    void testBuyCurrencyToExists() {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet = new Wallet("MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        this.service.buy(wallet, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE), currencyAmountTo.getAmount());
    }

    /**
     * Test buy currency to not exists.
     */
    @Test
    void testBuyCurrencyToNotExists() {
        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet = new Wallet("MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));
        this.service.buy(wallet, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE, currencyAmountTo.getAmount());
    }

    /**
     * Test validate buy.
     */
    @Test
    void testValidateBuy() {
        Exception exception1 = assertThrows(ValidationException.class, () -> service.buy(null, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception1.getMessage().contains("Wallet is required"));

        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet = new Wallet("MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));

        Exception exception2 = assertThrows(ValidationException.class, () -> service.buy(wallet, null, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception2.getMessage().contains("Currency from is required"));

        Exception exception3 = assertThrows(ValidationException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_FROM, null, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception3.getMessage().contains("Currency to is required"));

        Exception exception4 = assertThrows(ValidationException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, null));
        assertTrue(exception4.getMessage().contains("Amount is required"));

        Exception exception5 = assertThrows(ValidationException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, 0.0));
        assertTrue(exception5.getMessage().contains("Amount must be greater than zero"));

        Exception exception6 = assertThrows(NotFoundException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_NON_EXISTENT, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception6.getMessage().contains("Currency from not found"));

        Exception exception7 = assertThrows(NotFoundException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_NON_EXISTENT, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception7.getMessage().contains("Currency to not found"));

        Exception exception8 = assertThrows(ValidationException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_TO, CURRENCY_SYMBOL_FROM, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception8.getMessage().contains("Wallet does not contain the currency from"));

        Exception exception9 = assertThrows(ValidationException.class, () -> service.buy(wallet, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT + 0.1));
        assertTrue(exception9.getMessage().contains("The amount exceeds the available"));
    }

    /**
     * Test transfer currency to exists.
     */
    @Test
    void testTransferCurrencyToExists() {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet("MyWallet", Stream.of(currencyAmount1).collect(Collectors.toSet()));
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet2 = new Wallet("MyWallet", Stream.of(currencyAmount2).collect(Collectors.toSet()));
        this.service.transfer(wallet1, wallet2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE), currencyAmountTo.getAmount());
    }

    /**
     * Test transfer currency to not exists.
     */
    @Test
    void testTransferCurrencyToNotExists() {
        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet("MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));
        Wallet wallet2 = new Wallet("MyWallet", new HashSet<>());
        this.service.transfer(wallet1, wallet2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE, currencyAmountTo.getAmount());
    }
}
