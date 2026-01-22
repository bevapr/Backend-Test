package com.example.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Payment {

    @Id
    private String transactionId;
    private String orderId;
    private String userId;
    private double amount;
    private boolean completed;

}
