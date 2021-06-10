package com.sms.challenge.currencywalletapi.domain;

import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * The type Buy operation dto.
 */
@Data
public class TransferOperationDTO {

    private Long walletId;
    private String currencyFrom;
    private String currencyTo;
    private Double amount;
    @Nullable
    private Double price;
    @Nullable
    private Boolean validatePrice;
}
