package com.example.Cart.controller;


import com.example.Cart.dto.CartDto;
import com.example.Cart.dto.CartItemDto;
import com.example.Cart.service.CartService;
import com.example.Cart.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<CartDto>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/addToCart")
    public ResponseEntity<CommonResponse<CartItemDto>> addItem(@RequestParam Long userId, @RequestParam Long productOfferingId ,@RequestParam Integer quantity) {
        CartItemDto itemDto = new CartItemDto();
        itemDto.setQuantity(quantity);
        itemDto.setProductOfferingId(productOfferingId);
        return ResponseEntity.ok(cartService.addItemToCart(userId, itemDto));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CommonResponse<String>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(itemId));
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CommonResponse<String>> updateItem(@PathVariable Long itemId, @RequestBody CartItemDto itemDto) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, itemDto.getQuantity()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse<String>> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @GetMapping("/checkout/{userId}/{email}")
    public ResponseEntity<CommonResponse<String>> checkoutCart(@PathVariable Long userId, @PathVariable String email) {
        return ResponseEntity.ok(cartService.checkoutCart(userId, email));
    }
}
