package com.example.Order.service;

import com.example.Order.utils.CommonResponse;
import com.example.Order.dto.OrderDto;
import com.example.Order.dto.CartDto; // Import CartDto

import java.util.List;

public interface OrderService {
    CommonResponse<String> addOrder(Long userId, CartDto cartDto); // Update parameter type to CartDto
    CommonResponse<List<OrderDto>> getOrderHistory(Long userId);
}