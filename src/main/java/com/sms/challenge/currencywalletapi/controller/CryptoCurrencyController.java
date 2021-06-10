package com.sms.challenge.currencywalletapi.controller;

import com.sms.challenge.currencywalletapi.domain.CryptoCurrencyDTO;
import com.sms.challenge.currencywalletapi.domain.CryptoCurrencyPriceDTO;
import com.sms.challenge.currencywalletapi.service.CryptoCurrencyService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Crypto currency controller.
 */
@RestController
@RequestMapping("/crypto-currencies")
public class CryptoCurrencyController {

    @Autowired
    private CryptoCurrencyService service;

    /**
     * Find all response entity.
     *
     * @return the response entity
     */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval", response = CryptoCurrencyDTO.class, responseContainer = "List"),
            @ApiResponse(code = 503, message = "External service unavailable")})
    public ResponseEntity<List<CryptoCurrencyDTO>> findAll() {
        List<CryptoCurrencyDTO> result = this.service.findAll().stream().map(item -> {
            List<CryptoCurrencyPriceDTO> list = item.getPrices().stream().map(ccp -> CryptoCurrencyPriceDTO.builder().currency(
                    ccp.getCurrency()).price(ccp.getPrice()).build()).collect(Collectors.toList());
            return CryptoCurrencyDTO.builder().name(item.getCurrency()).prices(list).build();
        }).collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
