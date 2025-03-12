package com.example.Cart.service;

import com.example.Cart.client.OrderServiceClient;
import com.example.Cart.dto.CartDto;
import com.example.Cart.dto.CartItemDto;
import com.example.Cart.dto.EmailDetailsDto;
import com.example.Cart.entity.Cart;
import com.example.Cart.entity.CartItem;
import com.example.Cart.exception.ResourceNotFoundException;
import com.example.Cart.repository.CartItemRepository;
import com.example.Cart.repository.CartRepository;
import com.example.Cart.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RestTemplate restTemplate;


    //Email Functionality

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    private static final String ADD_ORDER_SERVICE_URL = "http://localhost:8083/orders/create/";

    public CommonResponse<CartDto> getCartByUserId(Long userId) { // **Add this method implementation**
        log.info("Fetching cart for user id: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));
        return CommonResponse.success(convertToDto(cart), 200, "Cart fetched successfully");
    }

    public CommonResponse<CartItemDto> addItemToCart(Long userId, CartItemDto itemDto) {
        log.info("Adding item to cart for user id: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        CartItem item = new CartItem();
        BeanUtils.copyProperties(itemDto, item);
        item.setCart(cart);
        cartItemRepository.save(item);

        return CommonResponse.success(itemDto, 201, "Item added to cart");
    }

    public CommonResponse<String> removeItemFromCart(Long itemId) {
        log.info("Removing item with id: {}", itemId);
        cartItemRepository.deleteById(itemId);
        return CommonResponse.success(null, 200, "Item removed from cart");
    }

    public CommonResponse<String> updateItemQuantity(Long itemId, Integer quantity) {
        log.info("Updating quantity for item id: {}", itemId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return CommonResponse.success(null, 200, "Item Quantity Updated");
    }

    @Transactional
    public CommonResponse<String> clearCart(Long userId) {
        log.info("Clearing cart for user id: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        if (items.isEmpty()) {
            log.info("No items to delete for cart id: {}", cart.getId());
        } else {
            cartItemRepository.deleteAll(items);
            log.info("Deleted {} items from cart id: {}", items.size(), cart.getId());
        }

        return CommonResponse.success(null, 200, "Cart cleared");
    }


    @Transactional
    public CommonResponse<String> checkoutCart(Long userId, String email) {
        log.info("Checking cart for user id: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        if (items.isEmpty()) {
            log.info("No items to buy for cart id: {}", cart.getId());
            return CommonResponse.failure("Cart is empty", 200);
        }

        log.info("Creating order for user id: {}", userId);

        CartDto cartDto = convertToDto(cart);

        try {
            ResponseEntity<CommonResponse<String>> responseEntity = restTemplate.exchange( // Use RestTemplate.exchange
                    ADD_ORDER_SERVICE_URL + userId, // Call orderService URL directly
                    HttpMethod.POST,
                    new HttpEntity<>(cartDto), // Send CartDto in the request body
                    new ParameterizedTypeReference<CommonResponse<String>>() {
                    } // Expected response type
            );
            CommonResponse<String> response = responseEntity.getBody();

            log.info(response.getData());

            log.info("Order created successfully. Clearing cart: {}", cart.getId());
            cartItemRepository.deleteByCartId(cart.getId());

            String res = sendSimpleEmail(new EmailDetailsDto("Order Confirmation", "Your order has been placed successfully", email));
            log.info(res);
            return CommonResponse.success(null, 200, "Cart Bought");

        } catch (Exception e) {
            log.error("Error during checkout for user id: {}", userId, e);
            return CommonResponse.failure("Failed to checkout cart. Please try again later.", 500);
        }
    }

    @Override
    public String sendSimpleEmail(EmailDetailsDto details) {
        try {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMessage());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            log.info("Sending mail with details: {}", details);
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }
    }


    private CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setUserId(cart.getUserId());
        cartDto.setItems(cart.getItems().stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList()));
        return cartDto;
    }

    private CartItemDto convertItemToDto(CartItem item) {
        return new CartItemDto(item.getId(), item.getProductOfferingId(), item.getQuantity());
    }

}