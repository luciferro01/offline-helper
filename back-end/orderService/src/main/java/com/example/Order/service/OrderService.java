package com.example.Order.service;

import com.example.Order.utils.CommonResponse;
import com.example.Order.dto.OrderDto;

import java.util.List;

public interface OrderService {
    CommonResponse<String> addOrder(Long cartId);
    CommonResponse<List<OrderDto>> getOrderHistory(Long userId);
}