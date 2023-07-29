package com.beloved.transactionservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementRequest {
    private String accountNumber;
    private String startDate;
    private String endDate;

}
