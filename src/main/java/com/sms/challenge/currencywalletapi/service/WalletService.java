package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import com.sms.challenge.currencywalletapi.persistence.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Wallet find(Long id) {
        // Input validations
        if (id == null) {
            throw new ValidationException("Id is required");
        }
        // Find wallet
        return this.walletRepository.findById(id).orElseThrow(() -> new NotFoundException("Wallet not found"));
    }

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
        if (wallet.getCurrencyAmounts().stream().anyMatch(item -> item.getAmount() == null)) {
            throw new ValidationException("Amount is required in currency amounts");
        }
        if (wallet.getCurrencyAmounts().stream().anyMatch(item -> item.getCurrency() == null)) {
            throw new ValidationException("Currency is required in currency amounts");
        }
        // Save wallet
        return walletRepository.save(wallet);
    }

    public Wallet update(Wallet wallet) {
        // Input validations
        if (wallet == null) {
            throw new ValidationException("Wallet is required");
        }
        if (wallet.getId() == null) {
            throw new ValidationException("Id is required");
        }
        Wallet persisted = this.walletRepository.findById(wallet.getId()).orElseThrow(() -> new NotFoundException("Wallet not found"));
        if (StringUtils.isEmpty(wallet.getName())) {
            throw new ValidationException("Name is required");
        }
        // Update wallet
        persisted.setName(wallet.getName());
        return walletRepository.save(persisted);
    }

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
