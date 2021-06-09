package com.sms.challenge.currencywalletapi.integration.service;

import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyOperationService;
import com.sms.challenge.currencywalletapi.service.WalletService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Crypto currency operation service test.
 */
@SpringBootTest
public class CryptoCurrencyOperationServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoCurrencyOperationServiceTest.class);

    private static final String CURRENCY_SYMBOL_FROM = "BTC";
    private static final String CURRENCY_SYMBOL_TO = "USD";
    private static final Double CURRENCY_INITIAL_AMOUNT_TO = 100.0;
    private static final Double CURRENCY_AMOUNT_TO_BUY = 5.0;
    private static final Double CURRENCY_PRICE = 20.0;
    private static final Integer THREAD_COUNT = 10;
    private static final Double CURRENCY_INITIAL_AMOUNT_FROM = CURRENCY_AMOUNT_TO_BUY * THREAD_COUNT;

    /**
     * The Service.
     */
    @Autowired
    CryptoCurrencyOperationService service;

    /**
     * The Wallet service.
     */
    @Autowired
    WalletService walletService;

    /**
     * Test buy.
     */
    @Test
    public void testBuy() {
        CurrencyAmount ca1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet = this.walletService.create(new Wallet("MyWallet", currencyAmounts));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(new Task(wallet.getId())));
        }

        futures.stream().forEach(item -> {
            try {
                item.get();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            } catch (ExecutionException e) {
                LOG.error(e.getMessage());
            }
        });

        wallet = this.walletService.find(wallet.getId());

        assertEquals(
                CURRENCY_INITIAL_AMOUNT_FROM - (CURRENCY_AMOUNT_TO_BUY * THREAD_COUNT),
                wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get().getAmount()
        );
        assertEquals(
                (CURRENCY_INITIAL_AMOUNT_TO + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE) * THREAD_COUNT),
                wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get().getAmount()
        );
    }

    /**
     * The type Task.
     */
    public class Task implements Callable<String> {
        private Long walletId;

        /**
         * Instantiates a new Task.
         *
         * @param walletId the wallet id
         */
        public Task(Long walletId) {
            this.walletId = walletId;
        }

        @Override
        public String call() throws Exception {
            service.buy(
                    this.walletId,
                    CURRENCY_SYMBOL_FROM,
                    CURRENCY_SYMBOL_TO,
                    CURRENCY_AMOUNT_TO_BUY,
                    CURRENCY_PRICE
            );
            return String.valueOf(System.currentTimeMillis());
        }
    }
}
