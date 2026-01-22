package com.example.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private String transactionId;
    private String orderId;
    private String userId;
    private Double amount;
    private boolean completed;

}
