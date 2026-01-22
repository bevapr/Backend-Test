package com.example.payment.service;

import com.example.payment.dto.PaymentResponse;
import com.example.payment.model.Payment;

public interface PaymentService {
    PaymentResponse createPayment(Payment payment);
}
