package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * The type Operation dto.
 */
@Data
public class BuyOperationDTO {

    private String currencyFrom;
    private String currencyTo;
    private Double amount;
    @Nullable
    private Double price;
    @Nullable
    private Boolean validatePrice;
}
