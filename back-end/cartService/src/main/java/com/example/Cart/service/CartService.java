package com.example.Cart.service;

import com.example.Cart.dto.CartDto;
import com.example.Cart.dto.CartItemDto;
import com.example.Cart.dto.EmailDetailsDto;
import com.example.Cart.entity.Cart;
import com.example.Cart.entity.CartItem;
import com.example.Cart.exception.ResourceNotFoundException;
import com.example.Cart.utils.CommonResponse;
import org.springframework.beans.BeanUtils;

public interface CartService {
    CommonResponse<CartDto> getCartByUserId(Long userId);

    CommonResponse<CartItemDto> addItemToCart(Long userId, CartItemDto itemDto);

    CommonResponse<String> removeItemFromCart(Long itemId);

    CommonResponse<String> updateItemQuantity(Long itemId, Integer quantity);

    CommonResponse<String> clearCart(Long userId);

    CommonResponse<String> checkoutCart(Long userId, String email);

    String sendSimpleEmail(EmailDetailsDto emailDetails);
}
