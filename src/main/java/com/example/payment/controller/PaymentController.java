package com.example.payment.controller;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.model.Payment;
import com.example.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        Payment payment = new Payment();
        payment.setTransactionId(request.getTransactionId());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());

        PaymentResponse response = paymentService.createPayment(payment);

        return ResponseEntity.ok(response);
    }
}
