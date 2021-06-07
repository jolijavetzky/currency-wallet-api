package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * The type Crypto currency operation service.
 */
@Service
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
     * @param wallet       the wallet
     * @param currencyFrom the currency from
     * @param currencyTo   the currency to
     * @param amount       the amount
     */
    public void buy(Wallet wallet, String currencyFrom, String currencyTo, Double amount) {
        // Input validations
        if (wallet == null) {
            throw new ValidationException("Wallet is required");
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
        if (amount.doubleValue() <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
        // Data validations
        if (this.currencyService.findBySymbol(currencyFrom) == null) {
            throw new NotFoundException("Currency from not found");
        }
        if (this.currencyService.findBySymbol(currencyTo) == null) {
            throw new NotFoundException("Currency to not found");
        }
        CurrencyAmount currencyAmountFrom = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyFrom)).findFirst().orElseThrow(() -> new ValidationException("Wallet does not contain the currency from"));
        if (amount > currencyAmountFrom.getAmount().doubleValue()) {
            throw new ValidationException("The amount exceeds the available");
        }
        // Conversion
        Double price = cryptoCurrencyService.convert(currencyFrom, currencyTo);
        // Update in wallet
        currencyAmountFrom.setAmount(currencyAmountFrom.getAmount() - amount);
        Optional<CurrencyAmount> optional = wallet.getCurrencyAmounts().stream().filter(item -> item.getCurrency().equals(currencyTo)).findFirst();
        if (optional.isPresent()) {
            CurrencyAmount currencyAmountTo = optional.get();
            currencyAmountTo.setAmount(currencyAmountTo.getAmount() + (price * amount));
        } else {
            CurrencyAmount newCurrencyAmount = new CurrencyAmount(currencyTo, price * amount);
            wallet.getCurrencyAmounts().add(newCurrencyAmount);
        }
        this.walletService.update(wallet);
    }
}
