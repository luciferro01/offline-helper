package com.example.Cart.client;

import com.example.Cart.config.FeignConfig;
import com.example.Cart.dto.CartDto;
import com.example.Cart.utils.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service",configuration = FeignConfig.class) // Name of the Order Service as registered in Eureka
public interface OrderServiceClient {

    @PostMapping(value = "/orders/create/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    CommonResponse<String> createOrder(@PathVariable("userId") Long userId, @RequestBody CartDto cart); // Use CartDto here
}