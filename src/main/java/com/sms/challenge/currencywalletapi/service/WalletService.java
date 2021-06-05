package com.sms.challenge.currencywalletapi.service;

import com.sms.challenge.currencywalletapi.domain.CurrencyAmountDTO;
import com.sms.challenge.currencywalletapi.domain.WalletDTO;
import com.sms.challenge.currencywalletapi.exception.NotFoundException;
import com.sms.challenge.currencywalletapi.exception.ValidationException;
import com.sms.challenge.currencywalletapi.persistence.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import com.sms.challenge.currencywalletapi.persistence.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Wallet service.
 */
@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    /**
     * Find wallet dto.
     *
     * @param id the id
     * @return the wallet dto
     */
    public WalletDTO find(Long id) {
        // Input validations
        if (id == null) {
            throw new ValidationException("Id is required");
        }
        // Find wallet
        return this.transform(this.walletRepository.findById(id).orElseThrow(() -> new NotFoundException("Wallet not found")));
    }

    /**
     * Create wallet dto.
     *
     * @param dto the dto
     * @return the wallet dto
     */
    public WalletDTO create(WalletDTO dto) {
        // Input validations
        if (dto == null) {
            throw new ValidationException("Wallet is required");
        }
        if (dto.getId() != null) {
            throw new ValidationException("Id is not allowed");
        }
        if (StringUtils.isEmpty(dto.getName())) {
            throw new ValidationException("Name is required");
        }
        if (dto.getCurrencyAmounts().stream().anyMatch(item -> item.getAmount() == null)) {
            throw new ValidationException("Amount is required in currency amounts");
        }
        if (dto.getCurrencyAmounts().stream().anyMatch(item -> item.getCurrency() == null)) {
            throw new ValidationException("Currency is required in currency amounts");
        }
        // Save wallet
        return transform(walletRepository.save(this.transform(dto)));
    }

    /**
     * Update wallet dto.
     *
     * @param dto the dto
     * @return the wallet dto
     */
    public WalletDTO update(WalletDTO dto) {
        // Input validations
        if (dto == null) {
            throw new ValidationException("Wallet is required");
        }
        if (dto.getId() == null) {
            throw new ValidationException("Id is required");
        }
        Wallet wallet = this.walletRepository.findById(dto.getId()).orElseThrow(() -> new NotFoundException("Wallet not found"));
        if (StringUtils.isEmpty(dto.getName())) {
            throw new ValidationException("Name is required");
        }
        if (!CollectionUtils.isEmpty(dto.getCurrencyAmounts())) {
            throw new ValidationException("Currency amounts are not allowed");
        }
        // Update wallet
        wallet.setName(dto.getName());
        return transform(walletRepository.save(wallet));
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

    private Wallet transform(WalletDTO walletDTO) {
        Wallet.Builder walletBuilder = Wallet.builder();
        CurrencyAmount.Builder currencyAmountBuilder = CurrencyAmount.builder();
        Set<CurrencyAmount> currencyAmounts = walletDTO.getCurrencyAmounts().stream().map(item -> currencyAmountBuilder.currency(item.getCurrency()).amount(item.getAmount()).build()).collect(Collectors.toSet());
        return walletBuilder.name(walletDTO.getName()).currencyAmounts(currencyAmounts).build();
    }

    private WalletDTO transform(Wallet wallet) {
        WalletDTO.Builder walletDTOBuilder = WalletDTO.builder();
        CurrencyAmountDTO.Builder currencyAmountDTOBuilder = CurrencyAmountDTO.builder();
        Set<CurrencyAmountDTO> currencyAmounts = wallet.getCurrencyAmounts().stream().map(item -> currencyAmountDTOBuilder.currency(item.getCurrency()).amount(item.getAmount()).build()).collect(Collectors.toSet());
        return walletDTOBuilder.id(wallet.getId()).name(wallet.getName()).currencyAmounts(currencyAmounts).build();
    }
}
