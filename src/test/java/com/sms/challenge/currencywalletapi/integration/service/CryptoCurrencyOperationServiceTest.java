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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
     * Test buy fixed price.
     */
    @Test
    public void testBuy_FixedPrice() {
        CurrencyAmount ca1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet = this.walletService.create(new Wallet("MyWallet", currencyAmounts));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(new BuyTask(wallet.getId(), CURRENCY_PRICE)));
        }

        this.collectFutures(futures);

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
     * Test buy without price.
     */
    @Test
    public void testBuy_WithoutPrice() {
        CurrencyAmount ca1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet = this.walletService.create(new Wallet("MyWallet", currencyAmounts));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(new BuyTask(wallet.getId())));
        }

        this.collectFutures(futures);

        wallet = this.walletService.find(wallet.getId());

        assertEquals(
                CURRENCY_INITIAL_AMOUNT_FROM - (CURRENCY_AMOUNT_TO_BUY * THREAD_COUNT),
                wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get().getAmount()
        );
        assertTrue(
                wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get().getAmount() > CURRENCY_INITIAL_AMOUNT_TO
        );
    }

    /**
     * Test transfer fixed price.
     */
    @Test
    public void testTransfer_FixedPrice() {
        CurrencyAmount ca1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts1 = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet1 = this.walletService.create(new Wallet("MyWallet", currencyAmounts1));

        CurrencyAmount ca3 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca4 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts2 = Stream.of(ca3, ca4).collect(Collectors.toSet());
        Wallet wallet2 = this.walletService.create(new Wallet("MyWallet", currencyAmounts2));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(new TransferTask(wallet1.getId(), wallet2.getId(), CURRENCY_PRICE)));
        }

        this.collectFutures(futures);

        wallet1 = this.walletService.find(wallet1.getId());
        wallet2 = this.walletService.find(wallet2.getId());

        assertEquals(
                CURRENCY_INITIAL_AMOUNT_FROM - (CURRENCY_AMOUNT_TO_BUY * THREAD_COUNT),
                wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get().getAmount()
        );
        assertEquals(
                (CURRENCY_INITIAL_AMOUNT_TO + (CURRENCY_AMOUNT_TO_BUY * CURRENCY_PRICE) * THREAD_COUNT),
                wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get().getAmount()
        );
    }

    /**
     * Test transfer without price.
     */
    @Test
    public void testTransfer_WithoutPrice() {
        CurrencyAmount ca1 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca2 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts1 = Stream.of(ca1, ca2).collect(Collectors.toSet());
        Wallet wallet1 = this.walletService.create(new Wallet("MyWallet", currencyAmounts1));

        CurrencyAmount ca3 = new CurrencyAmount(CURRENCY_SYMBOL_FROM, CURRENCY_INITIAL_AMOUNT_FROM);
        CurrencyAmount ca4 = new CurrencyAmount(CURRENCY_SYMBOL_TO, CURRENCY_INITIAL_AMOUNT_TO);
        Set<CurrencyAmount> currencyAmounts2 = Stream.of(ca3, ca4).collect(Collectors.toSet());
        Wallet wallet2 = this.walletService.create(new Wallet("MyWallet", currencyAmounts2));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(new TransferTask(wallet1.getId(), wallet2.getId())));
        }

        this.collectFutures(futures);

        wallet1 = this.walletService.find(wallet1.getId());
        wallet2 = this.walletService.find(wallet2.getId());

        assertEquals(
                CURRENCY_INITIAL_AMOUNT_FROM - (CURRENCY_AMOUNT_TO_BUY * THREAD_COUNT),
                wallet1.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_FROM)).findFirst().get().getAmount()
        );
        assertTrue(
                wallet2.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(CURRENCY_SYMBOL_TO)).findFirst().get().getAmount() > CURRENCY_INITIAL_AMOUNT_TO
        );
    }

    private void collectFutures(List<Future<String>> futures) {
        futures.stream().forEach(item -> {
            try {
                item.get();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            } catch (ExecutionException e) {
                LOG.error(e.getMessage());
            }
        });
    }

    /**
     * The type Buy task.
     */
    class BuyTask implements Callable<String> {
        private Long walletId;
        private Double price;

        /**
         * Instantiates a new Buy task.
         *
         * @param walletId the wallet id
         */
        public BuyTask(Long walletId) {
            this.walletId = walletId;
        }

        /**
         * Instantiates a new Buy task.
         *
         * @param walletId the wallet id
         * @param price    the price
         */
        public BuyTask(Long walletId, Double price) {
            this.walletId = walletId;
            this.price = price;
        }

        @Override
        public String call() throws Exception {
            service.buy(
                    this.walletId,
                    CURRENCY_SYMBOL_FROM,
                    CURRENCY_SYMBOL_TO,
                    CURRENCY_AMOUNT_TO_BUY,
                    this.price
            );
            return String.valueOf(System.currentTimeMillis());
        }
    }

    /**
     * The type Transfer task.
     */
    class TransferTask implements Callable<String> {
        private Long walletIdFrom;
        private Long walletIdTo;
        private Double price;

        /**
         * Instantiates a new Transfer task.
         *
         * @param walletIdFrom the wallet id from
         * @param walletIdTo   the wallet id to
         */
        public TransferTask(Long walletIdFrom, Long walletIdTo) {
            this.walletIdFrom = walletIdFrom;
            this.walletIdTo = walletIdTo;
        }

        /**
         * Instantiates a new Transfer task.
         *
         * @param walletIdFrom the wallet id from
         * @param walletIdTo   the wallet id to
         * @param price        the price
         */
        public TransferTask(Long walletIdFrom, Long walletIdTo, Double price) {
            this.walletIdFrom = walletIdFrom;
            this.walletIdTo = walletIdTo;
            this.price = price;
        }

        @Override
        public String call() throws Exception {
            service.transfer(
                    this.walletIdFrom,
                    this.walletIdTo,
                    CURRENCY_SYMBOL_FROM,
                    CURRENCY_SYMBOL_TO,
                    CURRENCY_AMOUNT_TO_BUY,
                    this.price
            );
            return String.valueOf(System.currentTimeMillis());
        }
    }
}
