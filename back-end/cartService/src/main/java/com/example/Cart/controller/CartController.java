package com.example.Cart.controller;


import com.example.Cart.dto.CartDto;
import com.example.Cart.dto.CartItemDto;
import com.example.Cart.service.CartService;
import com.example.Cart.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@CrossOrigin(origins = "*")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/getCart")
    public ResponseEntity<CommonResponse<CartDto>> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/addToCart")
    public ResponseEntity<CommonResponse<CartItemDto>> addItem(@RequestParam Long userId, @RequestParam Long productOfferingId ,@RequestParam Integer quantity) {
        CartItemDto itemDto = new CartItemDto();
        itemDto.setQuantity(quantity);
        itemDto.setProductOfferingId(productOfferingId);
        return ResponseEntity.ok(cartService.addItemToCart(userId, itemDto));
    }

    @DeleteMapping("/removeCartItem")
    public ResponseEntity<CommonResponse<String>> removeItem(@RequestParam Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(itemId));
    }

    @PutMapping("/updateCartItem")
    public ResponseEntity<CommonResponse<String>> updateItem(@RequestParam Long itemId, @RequestBody CartItemDto itemDto) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, itemDto.getQuantity()));
    }

    @DeleteMapping("/removeCart")
    public ResponseEntity<CommonResponse<String>> clearCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @GetMapping("/checkout")
    public ResponseEntity<CommonResponse<String>> checkoutCart(@RequestParam String email, @RequestParam Long userId) {
        return ResponseEntity.ok(cartService.checkoutCart(userId, email));
    }

    @PostMapping("/dummy")
    public ResponseEntity<CommonResponse<String>> dummy(@RequestParam Long userId) {
        log.info("Dummy endpoint hit for user id: {}", userId);
        return ResponseEntity.ok(CommonResponse.success(""+ userId, 200, ""));
    }
}
