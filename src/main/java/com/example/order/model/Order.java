package com.example.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Order {

    @Id
    private String orderId;
    private double amount;
    private String status;
}
