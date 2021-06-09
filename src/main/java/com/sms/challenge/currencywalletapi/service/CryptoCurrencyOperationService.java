package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.config.AppConfig;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * The type Crypto currency operation service.
 */
@Service
@Transactional
public class CryptoCurrencyOperationService {

    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AppConfig appConfig;

    /**
     * Buy.
     *
     * @param walletId     the wallet id
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @param amount       the amount
     */
    public void buy(Long walletId, String currencyFrom, String currencyTo, Double amount) {
        this.buy(walletId, currencyFrom, currencyTo, amount, null, null);
    }

    /**
     * Buy.
     *
     * @param walletId     the wallet id
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @param amount       the amount
     * @param price        the price
     */
    public void buy(Long walletId, String currencyFrom, String currencyTo, Double amount, Double price) {
        this.buy(walletId, currencyFrom, currencyTo, amount, price, Boolean.FALSE);
    }

    /**
     * Buy.
     *
     * @param walletId      the wallet id
     * @param currencyFrom  the currency from
     * @param currencyTo    the currency to
     * @param amount        the amount
     * @param price         the price
     * @param validatePrice the validate price
     */
    public void buy(Long walletId, String currencyFrom, String currencyTo, Double amount, Double price, Boolean validatePrice) {
        this.validateBuyInputs(walletId, currencyFrom, currencyTo, amount);
        Wallet wallet = this.walletService.findForWrite(walletId);
        // Data validations
        if (wallet == null) {
            throw new NotFoundException("Wallet not found");
        }
        if (this.currencyService.findBySymbol(currencyFrom) == null) {
            throw new NotFoundException("Currency from not found");
        }
        if (this.currencyService.findBySymbol(currencyTo) == null) {
            throw new NotFoundException("Currency to not found");
        }
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyFrom)).findFirst().orElseThrow(() -> new ValidationException("Wallet does not contain the currency from"));
        if (amount > currencyAmountFrom.getAmount()) {
            throw new ValidationException("The amount exceeds the available");
        }
        // Conversion
        Double value = this.getFinalPrice(currencyFrom, currencyTo, price, validatePrice);
        // Substracts the amount in the from currency
        currencyAmountFrom.setAmount(currencyAmountFrom.getAmount() - amount);
        // Update the amount in destination currency
        Optional<CurrencyAmount> optionalCurrencyAmount = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyTo)).findFirst();
        this.updateCurrencyAmountTo(wallet, currencyTo, amount, value, optionalCurrencyAmount);
        // Update the wallet
        this.walletService.update(wallet);
    }

    /**
     * Transfer.
     *
     * @param walletIdFrom the wallet id from
     * @param walletIdTo   the wallet id to
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @param amount       the amount
     */
    public void transfer(Long walletIdFrom, Long walletIdTo, String currencyFrom, String currencyTo, Double amount) {
        this.transfer(walletIdFrom, walletIdTo, currencyFrom, currencyTo, amount, null, null);
    }

    /**
     * Transfer.
     *
     * @param walletIdFrom the wallet id from
     * @param walletIdTo   the wallet id to
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @param amount       the amount
     * @param price        the price
     */
    public void transfer(Long walletIdFrom, Long walletIdTo, String currencyFrom, String currencyTo, Double amount, Double price) {
        this.transfer(walletIdFrom, walletIdTo, currencyFrom, currencyTo, amount, price, Boolean.FALSE);
    }

    /**
     * Transfer.
     *
     * @param walletIdFrom  the wallet id from
     * @param walletIdTo    the wallet id to
     * @param currencyFrom  the currency from
     * @param currencyTo    the currency to
     * @param amount        the amount
     * @param price         the price
     * @param validatePrice the validate price
     */
    public void transfer(Long walletIdFrom, Long walletIdTo, String currencyFrom, String currencyTo, Double amount, Double price, Boolean validatePrice) {
        this.validateTransferInputs(walletIdFrom, walletIdTo, currencyFrom, currencyTo, amount);
        Wallet walletFrom = this.walletService.findForWrite(walletIdFrom);
        Wallet walletTo = this.walletService.findForWrite(walletIdTo);
        // Data validations
        if (walletFrom == null) {
            throw new NotFoundException("Wallet from not found");
        }
        if (walletTo == null) {
            throw new NotFoundException("Wallet to not found");
        }
        if (this.currencyService.findBySymbol(currencyFrom) == null) {
            throw new NotFoundException("Currency from not found");
        }
        if (this.currencyService.findBySymbol(currencyTo) == null) {
            throw new NotFoundException("Currency to not found");
        }
        CurrencyAmount currencyAmountFrom = walletFrom.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyFrom)).findFirst().orElseThrow(() -> new ValidationException("Wallet from does not contain the currency from"));
        if (amount > currencyAmountFrom.getAmount()) {
            throw new ValidationException("The amount exceeds the available");
        }
        // Conversion
        Double value = this.getFinalPrice(currencyFrom, currencyTo, price, validatePrice);
        // Substracts the amount in the from currency
        currencyAmountFrom.setAmount(currencyAmountFrom.getAmount() - amount);
        // Update wallet from
        this.walletService.update(walletFrom);
        // Update the amount in destination currency
        Optional<CurrencyAmount> optionalCurrencyAmount = walletTo.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyTo)).findFirst();
        this.updateCurrencyAmountTo(walletTo, currencyTo, amount, value, optionalCurrencyAmount);
        // Update wallet to
        this.walletService.update(walletTo);
    }

    private Double getFinalPrice(String currencyFrom, String currencyTo, Double price, Boolean validatePrice) {
        Double value;
        if (price != null) {
            if (price <= 0) {
                throw new ValidationException("Price must be greater than zero");
            }
            if (validatePrice) {
                Double diff = price / cryptoCurrencyService.convert(currencyFrom, currencyTo);
                if (diff >= 1 + this.appConfig.getPriceTolerance() || diff <= 1 - this.appConfig.getPriceTolerance()) {
                    throw new ValidationException("The price is different from the official");
                }
            }
            value = price;
        } else {
            value = cryptoCurrencyService.convert(currencyFrom, currencyTo);
        }
        return value;
    }

    private void updateCurrencyAmountTo(Wallet wallet, String currency, Double amount, Double price, Optional<CurrencyAmount> optionalCurrencyAmount) {
        if (optionalCurrencyAmount.isPresent()) {
            CurrencyAmount currencyAmountTo = optionalCurrencyAmount.get();
            currencyAmountTo.setAmount(currencyAmountTo.getAmount() + (price * amount));
        } else {
            CurrencyAmount newCurrencyAmount = new CurrencyAmount(currency, price * amount);
            wallet.getCurrencyAmounts().add(newCurrencyAmount);
        }
    }

    private void validateBuyInputs(Long walletId, String currencyFrom, String currencyTo, Double amount) {
        if (walletId == null) {
            throw new ValidationException("Wallet id is required");
        }
        if (StringUtils.isEmpty(currencyFrom)) {
            throw new ValidationException("Currency from is required");
        }
        if (StringUtils.isEmpty(currencyTo)) {
            throw new ValidationException("Currency to is required");
        }
        if (amount == null) {
            throw new ValidationException("Amount is required");
        }
        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
    }

    private void validateTransferInputs(Long walletIdFrom, Long walletIdTo, String currencyFrom, String currencyTo, Double amount) {
        if (walletIdFrom == null) {
            throw new ValidationException("Wallet id from is required");
        }
        if (walletIdTo == null) {
            throw new ValidationException("Wallet id to is required");
        }
        if (StringUtils.isEmpty(currencyFrom)) {
            throw new ValidationException("Currency from is required");
        }
        if (StringUtils.isEmpty(currencyTo)) {
            throw new ValidationException("Currency to is required");
        }
        if (amount == null) {
            throw new ValidationException("Amount is required");
        }
        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
    }
}
