package com.example.Order.service;

import com.example.Order.dto.OrderDto;
//import com.example.Order.entity.Cart;
//import com.example.Order.entity.CartItem;
import com.example.Order.entity.Order;
import com.example.Order.exception.ResourceNotFoundException;
//import com.example.Order.repository.CartItemRepository;
//import com.example.Order.repository.CartRepository;
import com.example.Order.repository.OrderRepository;
import com.example.Order.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String CART_SERVICE_URL = "http://localhost:8082/carts/";
    private static final String PRODUCT_OFFERING_SERVICE_URL = "http://localhost:8081/seller/";

    public CommonResponse<String> addOrder(Long userId, Cart cart) {
        log.info("Creating order from user Id: {}", cart.getUserId());

//        CommonResponse<Cart> response = restTemplate.exchange(
//                CART_SERVICE_URL + userId,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<CommonResponse<Cart>>() {}
//        ).getBody();

//        Cart cart = response.getData();
        log.info("Fetched cart: {}", cart);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty or not found");
        }
        
        List<CartItem> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        cartItems.forEach(item -> {
            CommonResponse<ProductOffering> POresponse = restTemplate.exchange(
                    PRODUCT_OFFERING_SERVICE_URL + item.getProductOfferingId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<CommonResponse<ProductOffering>>() {}
            ).getBody();

            if (POresponse == null || POresponse.getData() == null) {
                throw new IllegalStateException("Product data is null for product id: " + item.getProductOfferingId());
            }
            ProductOffering product = POresponse.getData();

            if (product == null || product.getPrice() == null) {
                throw new IllegalStateException("Product data or price is null for product id: " + item.getProductOfferingId());
            }

            log.info("Product Stock {}", product.stock);
            log.info("Product Sold {}", product.sold);

            if(product.getStock() < item.getQuantity()){
                throw new IllegalArgumentException("Quantity higher than stock");
            }
            product.setStock(product.getStock() - item.getQuantity());
            product.setSold((product.getSold() == null ? item.getQuantity() : product.getSold()) + item.getQuantity());

            CommonResponse<ProductOffering> updateResponse = restTemplate.exchange(
                    PRODUCT_OFFERING_SERVICE_URL + "/offering/" + item.getProductOfferingId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(product),
                    new ParameterizedTypeReference<CommonResponse<ProductOffering>>() {}
            ).getBody();

            if (updateResponse == null || updateResponse.getData() == null) {
                throw new IllegalStateException("Failed to update product offering for product id: " + item.getProductOfferingId());
            }

            log.info("Updated product offering: {}", updateResponse.getData());

            Order order = new Order();
            order.setUserId(cart.getUserId());
            order.setProductOfferingId(item.getProductOfferingId());
            order.setQuantity(item.getQuantity());
            order.setPrice(product.getPrice());
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            orderRepository.save(order);
        });

        return CommonResponse.success(null, 200, "Order placed successfully");
    }

    public CommonResponse<List<OrderDto>> getOrderHistory(Long userId) {
        log.info("Fetching order history for user id: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user id: " + userId);
        }

        List<OrderDto> orderDtos = orders.stream()
                .map(order -> new OrderDto(order.getId(), order.getUserId(), order.getProductOfferingId(), order.getPrice(), order.getQuantity(), order.getCreatedAt()))
                .collect(Collectors.toList());

        return CommonResponse.success(orderDtos, 200, "Order history fetched successfully");
    }

    public static class Cart {
        private Long id;
        private Long userId;
        private List<CartItem> items;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<CartItem> getItems() {
            return items;
        }

        public void setItems(List<CartItem> items) {
            this.items = items;
        }
    }

    static class CartItem {
        private Long id;
        private Long productOfferingId;
        private Integer quantity;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProductOfferingId() {
            return productOfferingId;
        }

        public void setProductOfferingId(Long productOfferingId) {
            this.productOfferingId = productOfferingId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    static class ProductOffering {
        private Long id;
        private Long sellerId;
        private Long productId;
        private String sellerName;
        private String productName;
        private Double price;
        private Integer stock;
        private Integer sold = 0;
        private Integer rating = null;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getSellerId() {
            return sellerId;
        }

        public void setSellerId(Long sellerId) {
            this.sellerId = sellerId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getSellerName() {
            return sellerName;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public Integer getSold() {
            return sold;
        }

        public void setSold(Integer sold) {
            this.sold = sold;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
}


