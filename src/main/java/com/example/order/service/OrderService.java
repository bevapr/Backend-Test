package com.example.order.service;

import com.example.order.model.Order;

import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Optional<Order> findById(String orderId);
    Order save(Order order);
}