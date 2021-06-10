package com.sms.challenge.currencywalletapi.controller;

import com.sms.challenge.currencywalletapi.domain.*;
import com.sms.challenge.currencywalletapi.entity.CurrencyAmount;
import com.sms.challenge.currencywalletapi.entity.Wallet;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyOperationService;
import com.sms.challenge.currencywalletapi.service.WalletService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @Autowired
    private CryptoCurrencyOperationService operationService;

    /**
     * Find wallet dto.
     *
     * @param id the id
     * @return the wallet dto
     */
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval", response = WalletDTO.class),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Bad request")})
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
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Resource created", response = WalletDTO.class),
            @ApiResponse(code = 400, message = "Bad request")})
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Resource updated", response = WalletDTO.class),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Bad request")})
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
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Resource deleted"),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Bad request")})
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Buy response entity.
     *
     * @param id  the id
     * @param dto the dto
     * @return the response entity
     */
    @PostMapping("/{id}/buy")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Resource accepted", response = WalletDTO.class),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 503, message = "External service unavailable")})
    public ResponseEntity<WalletDTO> buy(@PathVariable("id") Long id, @RequestBody BuyOperationDTO dto) {
        this.operationService.buy(
                id,
                dto.getCurrencyFrom(),
                dto.getCurrencyTo(),
                dto.getAmount(),
                dto.getPrice(),
                dto.getValidatePrice()
        );
        Wallet wallet = this.service.find(id);
        return new ResponseEntity<>(this.toDTO(wallet), HttpStatus.ACCEPTED);
    }

    /**
     * Buy response entity.
     *
     * @param id  the id
     * @param dto the dto
     * @return the response entity
     */
    @PostMapping("/{id}/transfer")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Resource accepted"),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 503, message = "External service unavailable")})
    public ResponseEntity<HttpStatus> transfer(@PathVariable("id") Long id, @RequestBody TransferOperationDTO dto) {
        this.operationService.transfer(
                id,
                dto.getWalletId(),
                dto.getCurrencyFrom(),
                dto.getCurrencyTo(),
                dto.getAmount(),
                dto.getPrice(),
                dto.getValidatePrice()
        );
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private WalletDTO toDTO(Wallet wallet) {
        List<CurrencyAmountDTO> currencyAmounts = wallet.getCurrencyAmounts().stream().map(item -> new CurrencyAmountDTO(
                item.getCurrency(),
                item.getAmount()
        )).collect(Collectors.toList());
        return WalletDTO.builder().id(wallet.getId()).name(wallet.getName()).currencyAmounts(currencyAmounts).build();
    }

    private Wallet toEntity(CreateWalletDTO dto) {
        Set<CurrencyAmount> currencyAmounts = dto.getCurrencyAmounts().stream().map(item -> new CurrencyAmount(
                item.getCurrency(),
                item.getAmount()
        )).collect(Collectors.toSet());
        return Wallet.builder().name(dto.getName()).currencyAmounts(currencyAmounts).build();
    }

    private void mergeEntity(Wallet wallet, UpdateWalletDTO dto) {
        wallet.setName(dto.getName());
    }
}
