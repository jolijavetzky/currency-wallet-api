package com.sms.challenge.currencywalletapi.controller;

import com.sms.challenge.currencywalletapi.domain.CreateWalletDTO;
import com.sms.challenge.currencywalletapi.domain.CurrencyAmountDTO;
import com.sms.challenge.currencywalletapi.domain.UpdateWalletDTO;
import com.sms.challenge.currencywalletapi.domain.WalletDTO;
import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
import com.sms.challenge.currencywalletapi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Wallet controller.
 */
@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Autowired
    private WalletService service;

    /**
     * Find wallet dto.
     *
     * @param id the id
     * @return the wallet dto
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletDTO> find(@PathVariable("id") Long id) {
        Wallet wallet = service.find(id);
        return new ResponseEntity<>(this.toDTO(wallet), HttpStatus.OK);
    }

    /**
     * Create response entity.
     *
     * @param dto the dto
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<WalletDTO> create(@RequestBody CreateWalletDTO dto) {
        Wallet wallet = service.create(this.toEntity(dto));
        return new ResponseEntity<>(this.toDTO(wallet), HttpStatus.CREATED);
    }

    /**
     * Update response entity.
     *
     * @param id  the id
     * @param dto the dto
     * @return the response entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<WalletDTO> update(@PathVariable("id") Long id, @RequestBody UpdateWalletDTO dto) {
        Wallet wallet = service.find(id);
        this.mergeEntity(wallet, dto);
        wallet = this.service.update(wallet);
        return new ResponseEntity<>(this.toDTO(wallet), HttpStatus.OK);
    }

    /**
     * Delete response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private WalletDTO toDTO(Wallet wallet) {
        List<CurrencyAmountDTO> currencyAmounts = wallet.getCurrencyAmounts().stream().map(item -> new CurrencyAmountDTO(item.getCurrency(), item.getAmount())).collect(Collectors.toList());
        return WalletDTO.builder().id(wallet.getId()).name(wallet.getName()).currencyAmounts(currencyAmounts).build();
    }

    private Wallet toEntity(CreateWalletDTO dto) {
        Set<CurrencyAmount> currencyAmounts = dto.getCurrencyAmounts().stream().map(item -> new CurrencyAmount(item.getCurrency(), item.getAmount())).collect(Collectors.toSet());
        return Wallet.builder().name(dto.getName()).currencyAmounts(currencyAmounts).build();
    }

    private void mergeEntity(Wallet wallet, UpdateWalletDTO dto) {
        wallet.setName(dto.getName());
    }
}
