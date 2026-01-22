package com.example.order.service;

import com.example.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse updateStatus(String orderId, String status);
}
