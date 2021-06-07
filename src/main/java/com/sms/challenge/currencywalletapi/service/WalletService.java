package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import com.sms.challenge.currencywalletapi.persistence.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;

/**
 * The type Wallet service.
 */
@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    /**
     * Find wallet.
     *
     * @param id the id
     * @return the wallet
     */
    public Wallet find(Long id) {
        // Input validations
        if (id == null) {
            throw new ValidationException("Id is required");
        }
        // Find wallet
        return this.walletRepository.findById(id).orElseThrow(() -> new NotFoundException("Wallet not found"));
    }

    /**
     * Create wallet.
     *
     * @param wallet the wallet
     * @return the wallet
     */
    public Wallet create(Wallet wallet) {
        // Input validations
        if (wallet == null) {
            throw new ValidationException("Wallet is required");
        }
        if (wallet.getId() != null) {
            throw new ValidationException("Id is not allowed");
        }
        if (StringUtils.isEmpty(wallet.getName())) {
            throw new ValidationException("Name is required");
        }
        if (!CollectionUtils.isEmpty(wallet.getCurrencyAmounts()) && wallet.getCurrencyAmounts().stream().anyMatch(item -> item.getAmount() == null)) {
            throw new ValidationException("Amount is required in currency amounts");
        }
        if (!CollectionUtils.isEmpty(wallet.getCurrencyAmounts()) && wallet.getCurrencyAmounts().stream().anyMatch(item -> item.getCurrency() == null)) {
            throw new ValidationException("Currency is required in currency amounts");
        }
        // Save wallet
        return walletRepository.save(wallet);
    }

    /**
     * Update wallet.
     *
     * @param wallet the wallet
     * @return the wallet
     */
    public Wallet update(Wallet wallet) {
        // Input validations
        if (wallet == null) {
            throw new ValidationException("Wallet is required");
        }
        if (wallet.getId() == null) {
            throw new ValidationException("Id is required");
        }
        this.walletRepository.findById(wallet.getId()).orElseThrow(() -> new NotFoundException("Wallet not found"));
        if (StringUtils.isEmpty(wallet.getName())) {
            throw new ValidationException("Name is required");
        }
        // Update wallet
        return walletRepository.save(wallet);
    }

    /**
     * Delete.
     *
     * @param id the id
     */
    public void delete(Long id) {
        // Input validations
        if (id == null) {
            throw new ValidationException("Id is required");
        }
        Wallet wallet = this.walletRepository.findById(id).orElseThrow(() -> new NotFoundException("Wallet not found"));
        // Delete wallet
        this.walletRepository.delete(wallet);
    }
}
