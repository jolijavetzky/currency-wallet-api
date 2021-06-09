package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Create wallet dto.
 */
@Data
public class CreateWalletDTO {
    private String name;
    private List<CurrencyAmountDTO> currencyAmounts = new ArrayList<>();
}
