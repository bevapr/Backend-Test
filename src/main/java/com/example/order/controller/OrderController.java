package com.example.order.controller;

import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.model.Order;
import com.example.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setAmount(request.getAmount());

        Order saved = orderService.createOrder(order);
        return ResponseEntity.ok(new OrderResponse(saved.getOrderId(), saved.getStatus()));
    }

    @PostMapping("/update-status")
    public ResponseEntity<OrderResponse> updateStatus(@RequestParam String orderId,
                                                      @RequestParam String status) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderService.save(order);

        return ResponseEntity.ok(new OrderResponse(order.getOrderId(), order.getStatus()));
    }
}