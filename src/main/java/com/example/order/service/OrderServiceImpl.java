package com.example.order.service;

import com.example.order.dto.OrderResponse;
import com.example.order.model.Order;
import com.example.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found: {}", orderId);
                    return new RuntimeException("Order not found");
                });

        if (!order.getStatus().equals(status)) {
            order.setStatus(status);
            orderRepository.save(order);
            logger.info("Order status updated for orderId {}: {}", orderId, status);
        } else {
            logger.info("Order status for orderId {} already '{}', no update needed", orderId, status);
        }

        return new OrderResponse(order.getOrderId(), order.getStatus());
    }
}
