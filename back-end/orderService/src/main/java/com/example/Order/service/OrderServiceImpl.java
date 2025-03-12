package com.example.Order.service;

import com.example.Order.client.ProductOfferingServiceClient; // Import Feign Client
import com.example.Order.dto.CartDto;
import com.example.Order.dto.CartItemDto;
import com.example.Order.dto.OrderDto;
import com.example.Order.dto.ProductOfferingDto; // Import ProductOfferingDto
import com.example.Order.entity.Order;
import com.example.Order.exception.ResourceNotFoundException;
import com.example.Order.repository.OrderRepository;
import com.example.Order.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Remove RestTemplate import
import org.springframework.http.ResponseEntity; // Import ResponseEntity

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired // Remove RestTemplate Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductOfferingServiceClient productOfferingServiceClient; // Inject Feign Client

    //private static final String CART_SERVICE_URL = "http://localhost:8082/carts/";
    private static final String PRODUCT_OFFERING_UPDATE_URL = "http://localhost:8764/seller/offering/";

    public CommonResponse<String> addOrder(Long userId, CartDto cartDto) {
        log.info("Creating order from user Id: {}", cartDto.getUserId());

        if (cartDto == null || cartDto.getItems() == null || cartDto.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty or not found");
        }

        List<CartItemDto> cartItems = cartDto.getItems();

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        cartItems.forEach(item -> {
            // using Feign Client to get Product Offering
            CommonResponse<ProductOfferingDto> poResponse = productOfferingServiceClient.getProductOffering(item.getProductOfferingId());


            if (poResponse == null || poResponse.getData() == null) {
                throw new IllegalStateException("Product data is null for product id: " + item.getProductOfferingId());
            }
            ProductOfferingDto product = poResponse.getData();

            if (product == null || product.getPrice() == null) {
                throw new IllegalStateException("Product data or price is null for product id: " + item.getProductOfferingId());
            }

            log.info("Product Stock {}", product.getStock());
            log.info("Product Sold {}", product.getSold());

            if(product.getStock() < item.getQuantity()){
                throw new IllegalArgumentException("Quantity higher than stock");
            }
            product.setStock(product.getStock() - item.getQuantity());
            product.setSold((product.getSold() == null ? item.getQuantity() : product.getSold()) + item.getQuantity());

            // Im using Feign Client to update Product Offering
            //CommonResponse<ProductOfferingDto> updateResponse = productOfferingServiceClient.updateProductOffering(item.getProductOfferingId(), product);
            log.info("Updating product offering URL: {}", PRODUCT_OFFERING_UPDATE_URL + item.getProductOfferingId());//Logging
            ResponseEntity<CommonResponse<ProductOfferingDto>> updateResponseEntity = restTemplate.exchange(
                    PRODUCT_OFFERING_UPDATE_URL + item.getProductOfferingId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(product), // Send ProductOfferingDto in the request body
                    new ParameterizedTypeReference<CommonResponse<ProductOfferingDto>>() {}
            );
            CommonResponse<ProductOfferingDto> updateResponse = updateResponseEntity.getBody();



            if (updateResponse == null || updateResponse.getData() == null) {
                throw new IllegalStateException("Failed to update product offering for product id: " + item.getProductOfferingId());
            }

            log.info("Updated product offering: {}", updateResponse.getData());

            Order order = new Order();
            order.setUserId(cartDto.getUserId());
            order.setProductOfferingId(item.getProductOfferingId());
            order.setQuantity(item.getQuantity());
            order.setPrice(product.getPrice());
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            order.setProductOfferingName(product.getProductName());
            order.setProductImageUrl(product.getProductImageUrl());

            orderRepository.save(order);
        });

        return CommonResponse.success("Cart Bought", 200, "Order placed successfully");
    }

    public CommonResponse<List<OrderDto>> getOrderHistory(Long userId) {
        log.info("Fetching order history for user id: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user id: " + userId);
        }

        List<OrderDto> orderDtos = orders.stream()
                .map(order -> new OrderDto(order.getId(), order.getUserId(), order.getProductOfferingId(), order.getProductOfferingName(), order.getProductImageUrl(), order.getPrice(), order.getQuantity(), order.getCreatedAt()))
                .collect(Collectors.toList());

        return CommonResponse.success(orderDtos, 200, "Order history fetched successfully");
    }
}