package com.example.Order.controller;

import com.example.Order.dto.OrderDto;
import com.example.Order.service.OrderService;
import com.example.Order.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<CommonResponse<String>> createOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.addOrder(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<List<OrderDto>>> getOrderHistory(@PathVariable Long userId) {
        CommonResponse<List<OrderDto>> response = orderService.getOrderHistory(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
