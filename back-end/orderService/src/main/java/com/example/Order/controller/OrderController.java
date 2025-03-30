package com.example.Order.controller;

import com.example.Order.dto.OrderDto;
import com.example.Order.service.OrderService;
import com.example.Order.utils.CommonResponse;
import com.example.Order.dto.CartDto; // Import CartDto from Order Service's package
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/createOrder")
    public CommonResponse<String> addOrder(@RequestParam Long userId, @RequestBody CartDto cart) { // Use Order Service's CartDto
        return ResponseEntity.ok(orderService.addOrder(userId, cart)).getBody();
    }

    @GetMapping("/getOrders")
    public ResponseEntity<CommonResponse<List<OrderDto>>> getOrderHistory(@RequestParam Long userId) {
        CommonResponse<List<OrderDto>> response = orderService.getOrderHistory(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}