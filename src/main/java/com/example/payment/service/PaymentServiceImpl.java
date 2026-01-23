package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    public PaymentServiceImpl(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());

        boolean success = chargeGateway(payment);
        payment.setCompleted(success);
        paymentRepository.save(payment);

        if (success) {
            try {
                String updateOrderUrl = "http://localhost:8081/orders/update-status?orderId="
                        + payment.getOrderId() + "&status=PAID";
                restTemplate.postForObject(updateOrderUrl, null, String.class);
                logger.info("Order status updated via API for orderId {}", payment.getOrderId());
            } catch (Exception e) {
                logger.error("Failed to update order via API: {}", e.getMessage());
            }

            try {
                Map<String, String> notif = new HashMap<>();
                notif.put("userId", payment.getUserId());
                notif.put("message", "Payment berhasil!");
                restTemplate.postForObject("http://localhost:8082/notifications/send", notif, String.class);
                logger.info("Notification sent via API to userId {}", payment.getUserId());
            } catch (Exception e) {
                logger.error("Failed to send notification via API: {}", e.getMessage());
            }
        }

        return mapToResponse(payment);
    }

    private boolean chargeGateway(Payment payment) {
        try {
            Thread.sleep(500);
            logger.info("Charged payment gateway for transaction: {}", payment.getTransactionId());
            return true;
        } catch (Exception e) {
            logger.error("Payment gateway failed: {}", e.getMessage());
            return false;
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getTransactionId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.isCompleted()
        );
    }
}

