package com.beloved.identityservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String receiverAccountNumber;
    private String senderAccountNumber;
    private BigDecimal amount;


}
