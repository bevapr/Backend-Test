package com.example.order.controller;

import com.example.order.dto.OrderResponse;
import com.example.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/update-status")
    public ResponseEntity<OrderResponse> updateStatus(@RequestParam String orderId,
                                                      @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}
