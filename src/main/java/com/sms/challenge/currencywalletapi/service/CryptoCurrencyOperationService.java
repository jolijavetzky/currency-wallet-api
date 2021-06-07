package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
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

    /**
     * Buy.
     *
     * @param walletId     the wallet id
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @param amount       the amount
     */
    public void buy(Long walletId, String currencyFrom, String currencyTo, Double amount) {
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
        Double price = cryptoCurrencyService.convert(currencyFrom, currencyTo);
        // Update in wallet
        currencyAmountFrom.setAmount(currencyAmountFrom.getAmount() - amount);
        Optional<CurrencyAmount> optionalCurrencyAmount = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyTo)).findFirst();
        this.updateCurrencyAmountTo(wallet, currencyTo, amount, price, optionalCurrencyAmount);
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
        Double price = cryptoCurrencyService.convert(currencyFrom, currencyTo);
        // Update wallet from
        currencyAmountFrom.setAmount(currencyAmountFrom.getAmount() - amount);
        this.walletService.update(walletFrom);
        // Update wallet to
        Optional<CurrencyAmount> optionalCurrencyAmount = walletTo.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyTo)).findFirst();
        this.updateCurrencyAmountTo(walletTo, currencyTo, amount, price, optionalCurrencyAmount);
        this.walletService.update(walletTo);
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
