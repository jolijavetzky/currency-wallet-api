package com.sms.challenge.currencywalletapi.unit.service;

import com.sms.challenge.currencywalletapi.config.AppConfig;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.entity.Currency;
import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
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
class CryptoCurrencyOperationServiceTest {

    private static final String CURRENCY_SYMBOL_FROM = "BTC";
    private static final String CURRENCY_SYMBOL_TO = "USD";
    private static final Double CURRENCY_INITIAL_AMOUNT = 2.5;
    private static final Double CURRENCY_AMOUNT_TO_BUY = 1.2;
    private static final Double CURRENCY_PRICE = 36000.0;
    private static final Double CURRENCY_PRICE_DIFF_GREATER = 36000.004;
    private static final Double CURRENCY_PRICE_DIFF_GREATER_INVALID = 36051.54;
    private static final Double CURRENCY_PRICE_DIFF_LESSER_INVALID = 35949.994;
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
     * The App config.
     */
    @Mock
    AppConfig appConfig;

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
        when(this.appConfig.getPriceTolerance()).thenReturn(0.00005);
    }

    /**
     * Test buy currency to exists.
     */
    @Test
    void testBuy_CurrencyTo_Exists() {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id)).thenReturn(wallet);
        this.service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE), currencyAmountTo.getAmount());
    }

    /**
     * Test buy currency to not exists.
     */
    @Test
    void testBuy_CurrencyTo_NotExists() {
        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id)).thenReturn(wallet);
        this.service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE, currencyAmountTo.getAmount());
    }

    /**
     * Test buy with price without validation.
     */
    @Test
    void testBuy_WithPrice_WithoutValidation() {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id)).thenReturn(wallet);
        this.service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_GREATER);
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE_DIFF_GREATER), currencyAmountTo.getAmount());
    }

    /**
     * Test buy with price with validation.
     */
    @Test
    void testBuy_WithPrice_WithValidation() {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id)).thenReturn(wallet);
        this.service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_GREATER, Boolean.TRUE);
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE_DIFF_GREATER), currencyAmountTo.getAmount());
    }

    /**
     * Test buy with price invalid.
     */
    @Test
    void testBuy_WithPrice_Invalid() {
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount1, currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id)).thenReturn(wallet);

        Exception exception1 = assertThrows(ValidationException.class, () -> this.service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_GREATER_INVALID, Boolean.TRUE));
        assertTrue(exception1.getMessage().contains("The price is different from the official"));

        Exception exception2 = assertThrows(ValidationException.class, () -> this.service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_LESSER_INVALID, Boolean.TRUE));
        assertTrue(exception2.getMessage().contains("The price is different from the official"));
    }

    /**
     * Test validate buy.
     */
    @Test
    void testValidateBuy() {
        Exception exception1 = assertThrows(ValidationException.class, () -> service.buy(null, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception1.getMessage().contains("Wallet id is required"));

        Exception exception2 = assertThrows(NotFoundException.class, () -> service.buy(212121L, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception2.getMessage().contains("Wallet not found"));

        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        final long id = 1L;
        Wallet wallet = new Wallet(id, "MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id)).thenReturn(wallet);

        Exception exception3 = assertThrows(ValidationException.class, () -> service.buy(id, null, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception3.getMessage().contains("Currency from is required"));

        Exception exception4 = assertThrows(ValidationException.class, () -> service.buy(id, CURRENCY_SYMBOL_FROM, null, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception4.getMessage().contains("Currency to is required"));

        Exception exception5 = assertThrows(ValidationException.class, () -> service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, null));
        assertTrue(exception5.getMessage().contains("Amount is required"));

        Exception exception6 = assertThrows(ValidationException.class, () -> service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, 0.0));
        assertTrue(exception6.getMessage().contains("Amount must be greater than zero"));

        Exception exception7 = assertThrows(NotFoundException.class, () -> service.buy(id, CURRENCY_SYMBOL_NON_EXISTENT, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception7.getMessage().contains("Currency from not found"));

        Exception exception8 = assertThrows(NotFoundException.class, () -> service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_NON_EXISTENT, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception8.getMessage().contains("Currency to not found"));

        Exception exception9 = assertThrows(ValidationException.class, () -> service.buy(id, CURRENCY_SYMBOL_TO, CURRENCY_SYMBOL_FROM, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception9.getMessage().contains("Wallet does not contain the currency from"));

        Exception exception10 = assertThrows(ValidationException.class, () -> service.buy(id, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT + 0.1));
        assertTrue(exception10.getMessage().contains("The amount exceeds the available"));
    }

    /**
     * Test transfer currency to exists.
     */
    @Test
    void testTransfer_CurrencyTo_Exists() {
        final long id1 = 1L;
        final long id2 = 2L;
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet(id1, "MyWallet", Stream.of(currencyAmount1).collect(Collectors.toSet()));
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet2 = new Wallet(id2, "MyWallet", Stream.of(currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id1)).thenReturn(wallet1);
        when(this.walletService.findForWrite(id2)).thenReturn(wallet2);
        this.service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE), currencyAmountTo.getAmount());
    }

    /**
     * Test transfer currency to not exists.
     */
    @Test
    void testTransfer_CurrencyTo_NotExists() {
        final long id1 = 1L;
        final long id2 = 2L;
        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet(id1, "MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));
        Wallet wallet2 = new Wallet(id2, "MyWallet", new HashSet<>());
        when(this.walletService.findForWrite(id1)).thenReturn(wallet1);
        when(this.walletService.findForWrite(id2)).thenReturn(wallet2);
        this.service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY);
        CurrencyAmount currencyAmountFrom = wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE, currencyAmountTo.getAmount());
    }

    @Test
    void testTransfer_WithPrice_WithoutValidation() {
        final long id1 = 1L;
        final long id2 = 2L;
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet(id1, "MyWallet", Stream.of(currencyAmount1).collect(Collectors.toSet()));
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet2 = new Wallet(id2, "MyWallet", Stream.of(currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id1)).thenReturn(wallet1);
        when(this.walletService.findForWrite(id2)).thenReturn(wallet2);
        this.service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_GREATER);
        CurrencyAmount currencyAmountFrom = wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE_DIFF_GREATER), currencyAmountTo.getAmount());
    }

    @Test
    void testTransfer_WithPrice_WithValidation() {
        final long id1 = 1L;
        final long id2 = 2L;
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet(id1, "MyWallet", Stream.of(currencyAmount1).collect(Collectors.toSet()));
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet2 = new Wallet(id2, "MyWallet", Stream.of(currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id1)).thenReturn(wallet1);
        when(this.walletService.findForWrite(id2)).thenReturn(wallet2);
        this.service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_GREATER, Boolean.TRUE);
        CurrencyAmount currencyAmountFrom = wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get();
        CurrencyAmount currencyAmountTo = wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get();
        assertEquals(CURRENCY_INITIAL_AMOUNT - CURRENCY_AMOUNT_TO_BUY, currencyAmountFrom.getAmount());
        assertEquals(CURRENCY_INITIAL_AMOUNT + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE_DIFF_GREATER), currencyAmountTo.getAmount());
    }

    @Test
    void testTransfer_WithPrice_Invalid() {
        final long id1 = 1L;
        final long id2 = 2L;
        CurrencyAmount currencyAmount1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet(id1, "MyWallet", Stream.of(currencyAmount1).collect(Collectors.toSet()));
        CurrencyAmount currencyAmount2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet2 = new Wallet(id2, "MyWallet", Stream.of(currencyAmount2).collect(Collectors.toSet()));
        when(this.walletService.findForWrite(id1)).thenReturn(wallet1);
        when(this.walletService.findForWrite(id2)).thenReturn(wallet2);

        Exception exception1 = assertThrows(ValidationException.class, () -> this.service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_GREATER_INVALID, Boolean.TRUE));
        assertTrue(exception1.getMessage().contains("The price is different from the official"));

        Exception exception2 = assertThrows(ValidationException.class, () -> this.service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY, CURRENCY_PRICE_DIFF_LESSER_INVALID, Boolean.TRUE));
        assertTrue(exception2.getMessage().contains("The price is different from the official"));
    }

    /**
     * Test validate transfer.
     */
    @Test
    void testValidateTransfer() {
        final long id1 = 1L;
        final long id2 = 2L;

        Exception exception1 = assertThrows(ValidationException.class, () -> service.transfer(null, id1, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception1.getMessage().contains("Wallet id from is required"));

        Exception exception2 = assertThrows(ValidationException.class, () -> service.transfer(id1, null, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception2.getMessage().contains("Wallet id to is required"));

        when(this.walletService.findForWrite(id2)).thenReturn(new Wallet());
        Exception exception3 = assertThrows(NotFoundException.class, () -> service.transfer(212121L, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception3.getMessage().contains("Wallet from not found"));

        when(this.walletService.findForWrite(id1)).thenReturn(new Wallet());
        Exception exception4 = assertThrows(NotFoundException.class, () -> service.transfer(id1, 212121L, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception4.getMessage().contains("Wallet to not found"));

        CurrencyAmount currencyAmount = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT);
        Wallet wallet1 = new Wallet(id1, "MyWallet", Stream.of(currencyAmount).collect(Collectors.toSet()));
        Wallet wallet2 = new Wallet(id2, "MyWallet", new HashSet<>());
        when(this.walletService.findForWrite(id1)).thenReturn(wallet1);
        when(this.walletService.findForWrite(id2)).thenReturn(wallet2);

        Exception exception5 = assertThrows(ValidationException.class, () -> service.transfer(id1, id2, null, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception5.getMessage().contains("Currency from is required"));

        Exception exception6 = assertThrows(ValidationException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, null, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception6.getMessage().contains("Currency to is required"));

        Exception exception7 = assertThrows(ValidationException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, null));
        assertTrue(exception7.getMessage().contains("Amount is required"));

        Exception exception8 = assertThrows(ValidationException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, 0.0));
        assertTrue(exception8.getMessage().contains("Amount must be greater than zero"));

        Exception exception9 = assertThrows(NotFoundException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_NON_EXISTENT, CURRENCY_SYMBOL_TO, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception9.getMessage().contains("Currency from not found"));

        Exception exception10 = assertThrows(NotFoundException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_NON_EXISTENT, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception10.getMessage().contains("Currency to not found"));

        Exception exception11 = assertThrows(ValidationException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_TO, CURRENCY_SYMBOL_FROM, CURRENCY_AMOUNT_TO_BUY));
        assertTrue(exception11.getMessage().contains("Wallet from does not contain the currency from"));

        Exception exception12 = assertThrows(ValidationException.class, () -> service.transfer(id1, id2, CURRENCY_SYMBOL_FROM, CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT + 0.1));
        assertTrue(exception12.getMessage().contains("The amount exceeds the available"));
    }
}
