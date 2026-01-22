package com.example.payment.service;

import com.example.payment.dto.PaymentResponse;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final RestTemplate restTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponse createPayment(Payment payment) {
        Payment savedPayment = paymentRepository.findById(payment.getTransactionId())
                .orElseGet(() -> {
                    boolean success = chargeGateway(payment);
                    payment.setCompleted(success);
                    Payment p = paymentRepository.save(payment);
                    logger.info("Payment saved: {}", p.getTransactionId());
                    return p;
                });

        if (savedPayment.isCompleted()) {
            try {
                String orderUrl = "http://localhost:8081/orders/update-status?orderId=" + savedPayment.getOrderId() + "&status=PAID";
                restTemplate.postForObject(orderUrl, null, String.class);
                logger.info("Order updated for orderId: {}", savedPayment.getOrderId());
            } catch (Exception e) {
                logger.error("Order service timeout: {}", e.getMessage());
            }

            try {
                Map<String, String> notif = new HashMap<>();
                notif.put("userId", savedPayment.getUserId());
                notif.put("message", "Payment berhasil!");
                restTemplate.postForObject("http://localhost:8082/notifications/send", notif, String.class);
                logger.info("Notification sent to userId: {}", savedPayment.getUserId());
            } catch (Exception e) {
                logger.error("Notification service timeout: {}", e.getMessage());
            }
        }

        return mapToResponse(savedPayment);
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
